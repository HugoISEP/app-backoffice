package com.mycompany.myapp.service;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.mycompany.myapp.HugoIsepApp;
import com.mycompany.myapp.domain.*;
import com.mycompany.myapp.repository.*;
import com.mycompany.myapp.service.dto.MissionDTO;
import com.mycompany.myapp.service.mapper.MissionMapper;
import com.mycompany.myapp.service.view.MissionView;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import com.mycompany.myapp.web.rest.errors.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.auditing.AuditingHandler;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = HugoIsepApp.class)
@Transactional
public class MissionServiceTest {
    private ListAppender<ILoggingEvent> logWatcher;

    @SpyBean
    private MissionService missionService;

    @Autowired
    private MissionMapper missionMapper;

    @Autowired
    private MissionRepository missionRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @SpyBean
    private PositionService positionService;

    @Autowired
    private JobTypeRepository jobTypeRepository;

    @MockBean
    private UserService userService;

    @Autowired
    private AuditingHandler auditingHandler;

    @Mock
    private DateTimeProvider dateTimeProvider;

    private Company company;

    private User user;

    private Mission mission;

    private JobType jobType;

    private Position position;

    @BeforeEach
    public void setUp() {
        company = Company.builder()
            .name("O'Tacos")
            .color("#666666")
            .imagePath("/path/to/tacos")
            .emailTemplate("@otacos.fr")
            .build();
        mission = Mission.builder()
            .name("BNP")
            .company(company)
            .projectManagerEmail("hugo@bnp.fr")
            .build();
        jobType = JobType.builder()
            .company(company)
            .icon("react")
            .name("Otacos")
            .build();
        position = Position.builder()
            .mission(mission)
            .jobType(jobType)
            .description("Tacos cordon bleu, nuggets algérienne samuraï sans boisson")
            .placesNumber(666)
            .duration(1)
            .status(true)
            .build();

        user = new User();
        user.setLogin("login");
        user.setCompany(company);
        user.setAuthorities(Set.of(Authority.builder().name("ROLE_ADMIN").build()));

        company = companyRepository.saveAndFlush(company);
        mission.setCompany(company);
        user.setCompany(company);
        mission = missionRepository.saveAndFlush(mission);
        jobType.setCompany(company);
        jobType = jobTypeRepository.saveAndFlush(jobType);
        position.setMission(mission);
        position.setJobType(jobType);
        List<Position> positions = new ArrayList<>();
        positions.add(position);
        mission.setPositions(positions);
        mission = missionRepository.saveAndFlush(mission);
        position = mission.getPositions().stream().findFirst().get();

        when(userService.getUserWithAuthorities()).thenReturn(Optional.of(user));
        when(dateTimeProvider.getNow()).thenReturn(Optional.of(LocalDateTime.now()));
        auditingHandler.setDateTimeProvider(dateTimeProvider);
        this.logWatcher = new ListAppender<>();
        this.logWatcher.start();
        ((Logger) LoggerFactory.getLogger(PositionService.class)).addAppender(this.logWatcher);
    }

    @Test
    public void hasAuthorizationWrongCompanyFailure(){
        Company otherCompany = Company.builder()
            .name("Tacos King")
            .color("#999999")
            .imagePath("/path/to/bad")
            .emailTemplate("@tacos-king.fr")
            .build();
        otherCompany = companyRepository.saveAndFlush(otherCompany);
        user.setCompany(otherCompany);
        user.setAuthorities(Set.of(Authority.builder().name("ROLE_MANAGER").build()));

        assertThrows(AccessDeniedException.class, () -> missionService.hasAuthorization(mission.getId()));
    }

    @Test
    public void hasAuthorizationGetMissionByIdSuccess(){
        MissionDTO missionDTO = missionService.getById(mission.getId());

        assertEquals(missionDTO, missionMapper.asDTO(mission));
        verify(missionService, times(1)).hasAuthorization(anyLong());
    }

    @Test
    public void hasAuthorizationGetMissionByIdNotFound(){
        assertThrows(ResourceNotFoundException.class, () -> missionService.getById(666666L));
    }

    @Test
    public void createMissionWithIdFailure(){
        MissionDTO missionDTO = missionMapper.asDTO(mission);

        assertThrows(BadRequestAlertException.class, () -> missionService.createMission(missionDTO));
    }

    @Test
    public void createMissionSuccess(){
        MissionDTO missionDTO = MissionDTO.builder().projectManagerEmail("bob@gmail.com").name("Cléopâtre").build();

        MissionDTO missionDTOSaved = missionService.createMission(missionDTO);

        assertEquals(missionDTO.getName(), missionDTOSaved.getName());
        assertEquals(missionDTO.getProjectManagerEmail(), missionDTOSaved.getProjectManagerEmail());
        assertEquals(user.getCompany(), missionDTOSaved.getCompany());
    }

    @Test
    public void deleteMissionMissionNotFound(){
        assertThrows(ResourceNotFoundException.class, () -> missionService.deleteMission(666L));
    }

    @Test
    public void deleteMissionMissionSuccess(){
        missionService.deleteMission(mission.getId());
        Optional<Mission> missionDeleted = missionRepository.findById(mission.getId());

        assertNotNull(missionDeleted.get());
        assertNotNull(missionDeleted.get().getDeletedAt());
        assertNotNull(missionDeleted.get().getPositions().get(0).getDeletedAt());
        verify(positionService, times(1)).clearPositionCacheByCompany(any(Company.class));
    }

    @Test
    public void editMissionFailure(){
        MissionDTO missionWithoutId = MissionDTO.builder().name("i have no id").build();
        assertThrows(BadRequestAlertException.class, () -> missionService.editMission(missionWithoutId));
    }

    @Test
    public void editMissionSuccess(){
        String oldName = mission.getName();
        String oldProjectManagerEmail = mission.getProjectManagerEmail();
        MissionDTO missionDTO = MissionDTO.builder().id(mission.getId()).name("cb500").projectManagerEmail("bob@honda.fr").build();

        MissionDTO missionDTOSaved = missionService.editMission(missionDTO);

        assertEquals(missionDTOSaved.getName(), mission.getName());
        assertEquals(missionDTOSaved.getProjectManagerEmail(), mission.getProjectManagerEmail());
        assertNotEquals(missionDTOSaved.getName(), oldName);
        assertNotEquals(missionDTOSaved.getProjectManagerEmail(), oldProjectManagerEmail);
    }

    @Test
    public void getAllMissionByCompanySuccess(){
        Company otherCompany = Company.builder()
            .name("Tacos King")
            .color("#999999")
            .imagePath("/path/to/bad")
            .emailTemplate("@tacos-king.fr")
            .build();
        otherCompany = companyRepository.saveAndFlush(otherCompany);

        Mission otherMission = Mission.builder().name("in another company").projectManagerEmail("stranger@gmail.com").company(otherCompany).build();
        otherMission = missionRepository.saveAndFlush(otherMission);


        Pageable pageable = PageRequest.of(0, 10);
        Page<MissionView> missionViews = missionService.getAllMissionByCompany(pageable, "%%");
        List<Long> missionViewIds = missionViews.getContent().stream().map(MissionView::getId).sorted().collect(Collectors.toList());

        assertTrue(missionViewIds.contains(mission.getId()));
        assertFalse(missionViewIds.contains(otherMission.getId()));
    }

}
