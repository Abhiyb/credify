//package com.zeta.backend.service;
//
//import com.zeta.backend.model.CardApplication;
//import com.zeta.backend.model.UserProfile;
//import com.zeta.backend.repository.CardApplicationRepository;
//import com.zeta.backend.repository.UserProfileRepository;
//import com.zeta.backend.util.CardApprovalUtil;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.*;
//import java.time.LocalDate;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class CardApplicationServiceTest {
//
//    @InjectMocks
//    private CardApplicationServiceImpl cardApplicationService;
//
//    @Mock
//    private CardApplicationRepository cardApplicationRepository;
//
//    @Mock
//    private UserProfileRepository userProfileRepository;
//
//    @Captor
//    private ArgumentCaptor<CardApplication> applicationCaptor;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void testSubmitCardApplication_Success() {
//        // Given
//        Long userId = 1L;
//
//        UserProfile mockUser = UserProfile.builder()
//                .userId(userId)
//                .fullName("Test User")
//                .annualIncome(800000.0)
//                .build();
//
//        CardApplication application = new CardApplication();
//        application.setUser(mockUser);
//        application.setCardType("VISA");
//        application.setRequestedLimit(200000.0);
//
//        when(userProfileRepository.findById(userId)).thenReturn(Optional.of(mockUser));
//        when(cardApplicationRepository.save(any(CardApplication.class))).thenAnswer(i -> i.getArgument(0));
//
//        // When
//        CardApplication saved = cardApplicationService.apply(application);
//
//        // Then
//        assertNotNull(saved);
//        assertEquals("APPROVED", saved.getStatus());
//        assertEquals("VISA", saved.getCardType());
//        assertEquals(200000.0, saved.getRequestedLimit());
//        assertEquals(mockUser, saved.getUser());
//        assertEquals(LocalDate.now(), saved.getApplicationDate());
//
//        verify(cardApplicationRepository).save(applicationCaptor.capture());
//        CardApplication captured = applicationCaptor.getValue();
//        assertEquals("APPROVED", captured.getStatus());
//    }
//
//    @Test
//    void testGetApplicationByUserId_Success() {
//        Long userId = 2L;
//        UserProfile mockUser = new UserProfile();
//        mockUser.setUserId(userId);
//
//        CardApplication mockApp = new CardApplication();
//        mockApp.setCardType("MASTERCARD");
//        mockApp.setStatus("PENDING");
//        mockApp.setRequestedLimit(300000.0);
//        mockApp.setUser(mockUser);
//
//        when(cardApplicationRepository.findById(userId)).thenReturn(Optional.of(mockApp));
//
//        CardApplication result = cardApplicationService.getApplicationsByUserId(userId);
//
//        assertNotNull(result);
//        assertEquals("MASTERCARD", result.getCardType());
//        assertEquals("PENDING", result.getStatus());
//        assertEquals(300000.0, result.getRequestedLimit());
//    }
//
//    @Test
//    void testSubmitCardApplication_UserNotFound() {
//        Long userId = 100L;
//        CardApplication app = new CardApplication();
//        UserProfile user = new UserProfile();
//        user.setUserId(userId);
//        app.setUser(user);
//
//        when(userProfileRepository.findById(userId)).thenReturn(Optional.empty());
//
//        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
//            cardApplicationService.apply(app);
//        });
//
//        assertEquals("User not found with ID " + userId, ex.getMessage());
//
//    }
//
//    @Test
//    void testGetApplicationByUserId_NotFound() {
//        Long userId = 123L;
//        when(cardApplicationRepository.findById(userId)).thenReturn(Optional.empty());
//
//        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
//            cardApplicationService.getApplicationsByUserId(userId);
//        });
//
//        assertEquals("Card application not found", ex.getMessage());
//    }
//}
