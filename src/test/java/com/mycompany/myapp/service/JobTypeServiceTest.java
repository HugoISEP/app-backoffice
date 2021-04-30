package com.mycompany.myapp.service;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.mycompany.myapp.HugoIsepApp;
import com.mycompany.myapp.domain.*;
import com.mycompany.myapp.repository.CompanyRepository;
import com.mycompany.myapp.repository.JobTypeRepository;
import com.mycompany.myapp.repository.UserRepository;
import com.mycompany.myapp.service.dto.CompanyDTO;
import com.mycompany.myapp.service.dto.JobTypeDTO;
import com.mycompany.myapp.service.mapper.JobTypeMapper;
import com.mycompany.myapp.service.view.JobTypeView;
import com.mycompany.myapp.service.view.PositionView;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import com.mycompany.myapp.web.rest.errors.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = HugoIsepApp.class)
@Transactional
public class JobTypeServiceTest {

    private Long RANDOM_ID = 666666L;

    private ListAppender<ILoggingEvent> logWatcher;
    @SpyBean
    private JobTypeService jobTypeService;
    @Autowired
    private JobTypeRepository repository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JobTypeMapper mapper;
    @MockBean
    private UserService userService;
    @MockBean
    private DeviceService deviceService;
    @Autowired
    private CompanyRepository companyRepository;
    @MockBean
    private PositionService positionService;

    private User user;
    private JobType jobType;
    private Position position;
    private Company company;

    @BeforeEach
    public void setUp() {
        company = Company.builder()
            .name("O'Tacos")
            .color("#666666")
            .imagePath("/path/to/tacos")
            .emailTemplate("@otacos.fr")
            .users(Arrays.asList(user))
            .build();
        company = companyRepository.save(company);

        position = Position.builder()
            .duration(420)
            .description("RoadToPe")
            .placesNumber(420)
            .build();

        List<Position> positions = new ArrayList<>();
        positions.add(position);

        jobType = JobType.builder()
            .company(company)
            .positions(positions)
            .icon("react")
            .name("Otacos")
            .build();
        jobType = repository.save(jobType);

        user = new User();
        user.setId(420L);
        user.setLogin("login");
        user.setPassword("passwordpasswordpasswordpasswordpasswordpasswordpasswordsixt");
        List<JobType> jobTypes = new ArrayList<>();
        jobTypes.add(jobType);
        user.setJobTypes(jobTypes);
        user.setCompany(company);
        user.setAuthorities(Set.of(Authority.builder().name("ROLE_ADMIN").build()));
        user = userRepository.save(user);

        List<User> users = new ArrayList<>();
        users.add(user);
        company.setUsers(users);
        company = companyRepository.save(company);

        when(userService.getUserWithAuthorities()).thenReturn(Optional.of(user));

        this.logWatcher = new ListAppender<>();
        this.logWatcher.start();
        ((Logger) LoggerFactory.getLogger(PositionService.class)).addAppender(this.logWatcher);
    }

    @Test
    public void hasAuthorizationFailureBadCompany() {
        Company otherCompany = companyRepository.saveAndFlush(Company.builder()
            .name("Tacos Kink")
            .color("#666667")
            .imagePath("/path/to/bad/tacos")
            .emailTemplate("@tacosking.fr")
            .build());
        user.setAuthorities(Set.of(Authority.builder().name("ROLE_MANAGER").build()));
        user.setCompany(otherCompany);

        assertThrows(AccessDeniedException.class, () -> jobTypeService.getById(jobType.getId()));
    }

    @Test
    public void getByIdSuccess() {
        JobTypeDTO jobTypeDTO = jobTypeService.getById(jobType.getId());
        assertEquals(jobTypeDTO, mapper.asDTO(jobType));
    }

    @Test
    public void getByIdFailure() {
        assertThrows(ResourceNotFoundException.class, () -> jobTypeService.getById(RANDOM_ID));
    }

    @Test
    public void getAllJobTypesPaginatedSuccess(){
        Company otherCompany = companyRepository.saveAndFlush(Company.builder()
            .name("Tacos Kink")
            .color("#666667")
            .imagePath("/path/to/bad/tacos")
            .emailTemplate("@tacosking.fr")
            .build());
        otherCompany = companyRepository.save(otherCompany);

        JobType jobType1 = JobType.builder()
            .company(company)
            .icon("symfony")
            .name("Symfony")
            .build();
        jobType1 = repository.save(jobType1);
        JobType jobTypeAnotherMission = JobType.builder()
            .company(otherCompany)
            .icon("sun")
            .name("sun")
            .build();
        jobTypeAnotherMission = repository.save(jobTypeAnotherMission);

        String searchTerm = "symfony";
        Pageable pageable = PageRequest.of(0, 10);
        Page<JobTypeView> jobTypes = jobTypeService.getAllJobTypeByUserPaginated(pageable, searchTerm);
        assertEquals(jobTypes.getContent().size(), 1);
        assertNotEquals(jobTypes.getContent().get(0).getId(), jobTypeAnotherMission.getId());
        assertEquals(jobTypes.getContent().get(0).getId(), jobType1.getId());
    }

    @Test
    public void getAllJobTypeByUserSuccess(){
        JobType jobType1 = JobType.builder()
            .company(company)
            .icon("symfony")
            .name("Symfony")
            .build();
        jobType1 = repository.save(jobType1);
        user.getJobTypes().add(jobType1);
        user = userRepository.save(user);

        List<JobTypeView> jobTypes = jobTypeService.getAllJobTypeByUser();

        assertEquals(jobTypes.stream().map(JobTypeView::getId).sorted().collect(Collectors.toList()), user.getJobTypes().stream().map(JobType::getId).sorted().collect(Collectors.toList()));
    }

    @Test
    public void createJobTypeWithIdFailure(){
        JobTypeDTO jobTypeWithId = JobTypeDTO.builder().id(666L).build();
        assertThrows(BadRequestAlertException.class, () -> jobTypeService.createJobType(jobTypeWithId));
    }

    @Test
    public void createJobTypeSuccess(){
        JobTypeDTO jobTypeDTO = JobTypeDTO.builder().name("powerball").icon("symfony").company(CompanyDTO.builder().id(company.getId()).build()).build();
        doNothing().when(deviceService).subscribeAllUsersToNewTopic(isA(Long.class));

        JobTypeDTO jobTypeDTOSaved = jobTypeService.createJobType(jobTypeDTO);

        assertEquals(jobTypeDTO.getName(), jobTypeDTOSaved.getName());
        assertEquals(jobTypeDTO.getIcon(), jobTypeDTOSaved.getIcon());
        verify(deviceService, times(1)).subscribeAllUsersToNewTopic(jobTypeDTOSaved.getId());
        verify(jobTypeService, times(1)).clearJobTypeCacheByCompany(company);
    }

    @Test
    public void editJobTypeWithoutIdFailure(){
        JobTypeDTO jobTypeDTO = JobTypeDTO.builder().build();

        assertThrows(BadRequestAlertException.class, () -> jobTypeService.editJobType(jobTypeDTO));
    }

    @Test
    public void editJobTypeWithWrongIdFailure(){
        JobTypeDTO jobTypeDTO = JobTypeDTO.builder().id(666666L).build();

        assertThrows(ResourceNotFoundException.class, () -> jobTypeService.editJobType(jobTypeDTO));
    }

    @Test
    public void editJobTypeSuccess(){
        String oldName = jobType.getName();
        String oldIcon = jobType.getIcon();
        JobTypeDTO jobTypeDTO = JobTypeDTO.builder().id(jobType.getId()).name("powerball").icon("symfony").build();

        JobTypeDTO jobTypeDTOSaved = jobTypeService.editJobType(jobTypeDTO);
        System.out.println("After: " + jobType.getName() + " " + jobType.getIcon());

        verify(jobTypeService, times(1)).clearJobTypeCacheByCompany(company);
        verify(positionService, times(1)).clearPositionCacheByCompany(company);
        verify(jobTypeService, times(1)).hasAuthorization(anyLong());
        assertEquals(jobTypeDTOSaved.getName(), jobType.getName());
        assertEquals(jobTypeDTOSaved.getIcon(), jobType.getIcon());
        assertNotEquals(oldName, jobType.getName());
        assertNotEquals(oldIcon, jobType.getIcon());
    }

    @Test
    public void deleteJobTypeNotFoundFailure(){
        assertThrows(ResourceNotFoundException.class, () -> jobTypeService.deleteJobType(66666L));
    }

    @Test
    public void deleteJobTypeSuccess(){
        Long JobTypeId = jobType.getId();

        jobTypeService.deleteJobType(JobTypeId);

        Optional<JobType> jobTypeDeleted = repository.findById(JobTypeId);
        assertTrue(jobTypeDeleted.isPresent());
        assertTrue(Objects.nonNull(jobTypeDeleted.get().getDeletedAt()));
        assertTrue(Objects.nonNull(jobTypeDeleted.get().getPositions().get(0).getDeletedAt()));
        assertTrue(user.getJobTypes().isEmpty());
        verify(jobTypeService, times(1)).clearJobTypeCacheByCompany(company);
        verify(deviceService, times(1)).unsubscribeAllUsersDeletedTopic(jobTypeDeleted.get());
    }

    @Test
    public void clearJobTypeCacheByPositionSuccess(){
        List<JobTypeView> jobTypesViewsBefore = repository.findAllByCompany_Id(company.getId());
        jobTypeService.deleteJobType(jobType.getId());
        List<JobTypeView> jobTypesViewsAfter = repository.findAllByCompany_Id(jobType.getId());
        assertNotEquals(jobTypesViewsBefore, jobTypesViewsAfter);
        assertFalse(jobTypesViewsAfter.stream().anyMatch(j -> j.getId().equals(position.getId())));
    }

}
