package com.mycompany.myapp.service;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.mycompany.myapp.HugoIsepApp;
import com.mycompany.myapp.domain.*;
import com.mycompany.myapp.repository.*;
import com.mycompany.myapp.service.dto.PositionDTO;
import com.mycompany.myapp.service.notification.NotificationStatus;
import com.mycompany.myapp.service.view.PositionView;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import com.mycompany.myapp.web.rest.errors.ResourceNotFoundException;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import com.mycompany.myapp.service.mapper.PositionMapper;
import com.mycompany.myapp.service.notification.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.auditing.AuditingHandler;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = HugoIsepApp.class)
@Transactional
public class PositionServiceTest {

    private ListAppender<ILoggingEvent> logWatcher;

    @SpyBean
    private PositionService positionService;

    @Autowired
    private PositionMapper positionMapper;

    @Autowired
    private MissionRepository missionRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private JobTypeRepository jobTypeRepository;

    @MockBean
    private UserService userService;

    @MockBean
    private NotificationService notificationService;

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
        user.setId(420L);
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
    public void hasAuthorizationFailureBadCompany() {
        Company otherCompany = companyRepository.saveAndFlush(Company.builder()
            .name("Tacos Kink")
            .color("#666667")
            .imagePath("/path/to/bad/tacos")
            .emailTemplate("@tacosking.fr")
            .build());
        user.setAuthorities(Set.of(Authority.builder().name("ROLE_MANAGER").build()));
        user.setCompany(otherCompany);

        assertThrows(AccessDeniedException.class, () -> positionService.getById(position.getId()));
    }

    @Test
    public void hasAuthorizationGetPositionByIdSuccessAdmin() {
        user.setAuthorities(Set.of(Authority.builder().name("ROLE_ADMIN").build()));

        PositionDTO positionDTO = positionService.getById(position.getId());

        assertEquals(positionDTO, positionMapper.asDto(position));
    }

    @Test
    public void hasAuthorizationGetPositionByIdSuccessManager() {
        user.setAuthorities(Set.of(Authority.builder().name("ROLE_MANAGER").build()));
        user.setCompany(company);

        PositionDTO positionDTO = positionService.getById(position.getId());

        assertEquals(positionDTO, positionMapper.asDto(position));
    }

    @Test
    public void hasAuthorizationSuccessManager() {
        user.setAuthorities(Set.of(Authority.builder().name("ROLE_MANAGER").build()));
        user.setCompany(company);

        PositionDTO positionDTO = positionService.getById(position.getId());

        assertEquals(positionDTO, positionMapper.asDto(position));
    }

    @Test
    public void getByIdFailure() {
        assertThrows(ResourceNotFoundException.class, () -> positionService.getById(666666L));
    }

    @Test
    public void getPositionsByMissionIdSuccess() {
        // Add another position to the mission
        Position position2 = Position.builder()
            .mission(mission)
            .jobType(jobType)
            .description("Tacos cordon bleu, nuggets algérienne samuraï sans boisson")
            .placesNumber(666)
            .duration(1)
            .build();
        mission.getPositions().add(position2);
        mission = missionRepository.saveAndFlush(mission);

        // Add position to another mission to test that it's not retrieve
        Mission otherMission = Mission.builder()
            .name("Another mission")
            .company(company)
            .projectManagerEmail("hugo@bnp.fr")
            .build();
        Position positionInAnotherMission = Position.builder()
            .mission(otherMission)
            .jobType(jobType)
            .description("Tacos cordon bleu, nuggets algérienne samuraï sans boisson")
            .placesNumber(666)
            .duration(1)
            .build();
        List<Position> listPositions = new ArrayList<>();
        listPositions.add(positionInAnotherMission);
        otherMission.setPositions(listPositions);
        otherMission = missionRepository.saveAndFlush(otherMission);

        List<PositionView> positions = positionService.getByMissionId(mission.getId());

        assertTrue(positions.stream().map(PositionView::getId).collect(Collectors.toList()).equals(mission.getPositions().stream().map(Position::getId).collect(Collectors.toList())));
        assertFalse(positions.contains(otherMission.getPositions().stream().findFirst().get()));
    }

    @Test
    public void getActivePositionsByUserSuccess(){
        Position anotherPositionNotActive = Position.builder()
            .mission(mission)
            .jobType(jobType)
            .description("Tacos cordon bleu, nuggets algérienne samuraï sans boisson")
            .placesNumber(666)
            .duration(1)
            .status(false)
            .build();
        mission.getPositions().add(anotherPositionNotActive);
        mission = missionRepository.saveAndFlush(mission);

        // Add position to another mission to test that it's not retrieve
        Mission otherMission = Mission.builder()
            .name("Another mission")
            .company(company)
            .projectManagerEmail("hugo@bnp.fr")
            .build();
        Position positionInAnotherMission = Position.builder()
            .mission(otherMission)
            .jobType(jobType)
            .description("Tacos cordon bleu, nuggets algérienne samuraï sans boisson")
            .placesNumber(666)
            .duration(1)
            .build();
        List<Position> listPositions = new ArrayList<>();
        listPositions.add(positionInAnotherMission);
        otherMission.setPositions(listPositions);
        otherMission = missionRepository.saveAndFlush(otherMission);

        String searchTerm = "BNP";
        Pageable pageable = PageRequest.of(0, 10);
        List<PositionView> activePositions = positionService.getActivePositionsByUser(pageable, searchTerm).getContent();

        assertTrue(activePositions.stream().map(PositionView::getId).collect(Collectors.toList()).equals(mission.getPositions().stream().filter(Position::isStatus).map(Position::getId).collect(Collectors.toList())));
        assertFalse(activePositions.contains(otherMission.getPositions().stream().findFirst().get()));
    }

    @Test
    public void addPositionWithIdFailure(){
        PositionDTO positionWithId = PositionDTO.builder().id(666L).build();
        assertThrows(BadRequestAlertException.class, () -> positionService.addPosition(mission.getId(), positionWithId));
    }

    @Test
    public void addPositionNotificationFailure() throws ExecutionException, InterruptedException {
        PositionDTO positionDTO = PositionDTO.builder()
            .placesNumber(1)
            .status(true)
            .duration(1)
            .description("Lasagnes")
            .build();

        doThrow(new InterruptedException()).when(notificationService).sendMessage(any(), any());
        positionService.addPosition(mission.getId(), positionDTO);
        assertEquals(logWatcher.list.stream().findFirst().get().getFormattedMessage(), "Error when sending notification: java.lang.InterruptedException");
    }

    @Test
    public void addPositionSuccess() {
        PositionDTO positionDTO = PositionDTO.builder()
            .placesNumber(1)
            .duration(1)
            .description("Pates carbo")
            .build();

        PositionDTO positionReturn = positionService.addPosition(mission.getId(), positionDTO);
        verify(positionService, times(1)).clearPositionCacheByPosition(position.getMission().getCompany().getId());
        assertEquals(positionReturn.getDescription(), positionDTO.getDescription());
        assertEquals(positionReturn.getPlacesNumber(), positionDTO.getPlacesNumber());
        assertEquals(positionReturn.getDuration(), positionDTO.getDuration());
        assertNotNull(positionReturn.getId());
    }

    @Test
    public void editPositionWithoutIdFailure(){
        PositionDTO positionDTO = PositionDTO.builder()
            .placesNumber(1)
            .duration(1)
            .description("Pizza chèvre miel")
            .build();
        assertThrows(BadRequestAlertException.class,() -> positionService.editPosition(positionDTO));
    }

    @Test
    public void editPositionSuccess(){
        PositionDTO positionDTO = PositionDTO.builder()
            .id(position.getId())
            .placesNumber(22)
            .status(false)
            .duration(1999)
            .description("Pizza chèvre miel")
            .build();
        PositionDTO positionReturn = positionService.editPosition(positionDTO);
        verify(positionService, times(1)).clearPositionCacheByPosition(position.getMission().getCompany().getId());
        assertEquals(positionReturn.getId(), positionDTO.getId());
        assertEquals(positionReturn.getPlacesNumber(), positionDTO.getPlacesNumber());
        assertEquals(positionReturn.getDuration(), positionDTO.getDuration());
        assertEquals(positionReturn.getDescription(), positionDTO.getDescription());
        assertEquals(positionReturn.isStatus(), positionDTO.isStatus());
    }

    @Test
    public void sendNotificationBadStatusFailure() {
        position.setStatus(false);
        position = positionRepository.saveAndFlush(position);
        assertThrows(Exception.class,() -> positionService.sendNotification(position.getId()));
    }

    @Test
    public void sendNotificationRestrictionDelayFailure() throws Exception {
        positionService.sendNotification(position.getId());
        assertThrows(Exception.class,() -> positionService.sendNotification(position.getId()));
    }

    @Test
    public void sendNotificationSendingErrorFailure() throws Exception {
        doThrow(new InterruptedException()).when(notificationService).sendMessage(any(), any());
        positionService.sendNotification(position.getId());
        assertEquals(logWatcher.list.stream().findFirst().get().getFormattedMessage(), "Error when sending notification: java.lang.InterruptedException");
    }

    @Test
    public void sendNotificationSuccess() throws Exception {
        LocalDateTime previousLastNotificationAt = position.getLastNotificationAt();
        positionService.sendNotification(position.getId());
        verify(notificationService, times(1)).sendMessage(position, NotificationStatus.OLD);
        assertNotEquals(previousLastNotificationAt, position.getLastNotificationAt());
    }

    @Test
    public void deletePositionSuccess(){
        positionService.deletePosition(position.getId());
        Optional<Position> emptyPosition = positionRepository.findById(position.getId());
        Optional<Position> deletedPosition = positionRepository.findByIdAndDeletedAtIsNotNull(position.getId());
        assertFalse(emptyPosition.isPresent());
        assertTrue(deletedPosition.isPresent());
        assertNotNull(deletedPosition.get().getDeletedAt());
        verify(positionService, times(1)).clearPositionCacheByPosition(position.getMission().getCompany().getId());
    }

    @Test
    public void clearPositionCacheByPositionSuccess(){
        Pageable pageable = PageRequest.of(0, 10);
        List<PositionView> positionViewsBefore = positionRepository.findAllByMissionCompanyIdAndStatusIsTrue(position.getMission().getCompany().getId(), "%%",  pageable).getContent();
        positionService.deletePosition(position.getId());
        List<PositionView> positionViewsAfter = positionRepository.findAllByMissionCompanyIdAndStatusIsTrue(position.getMission().getCompany().getId(), "%%",  pageable).getContent();
        assertNotEquals(positionViewsBefore, positionViewsAfter);
        assertFalse(positionViewsAfter.stream().anyMatch(p -> p.getId().equals(position.getId())));
    }
}
