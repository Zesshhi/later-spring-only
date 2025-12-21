package ru.practicum.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.exception.NotFoundException;
import ru.practicum.item.Item;
import ru.practicum.item.ItemRepository;
import ru.practicum.request.dto.ItemRequestCreateDto;
import ru.practicum.request.dto.ItemRequestResponseDto;
import ru.practicum.user.User;
import ru.practicum.user.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private ItemRequestService itemRequestService;

    private User requester;
    private ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        requester = new User(1L, "user@example.com", "User");
        itemRequest = new ItemRequest();
        itemRequest.setId(10L);
        itemRequest.setRequester(requester);
        itemRequest.setDescription("Need a drill");
        itemRequest.setCreated(LocalDateTime.now());
    }

    @Test
    void createRequest_savesRequestWithRequesterAndDescription() {
        ItemRequestCreateDto createDto = new ItemRequestCreateDto("Need a drill");

        when(userService.getUserById(1L)).thenReturn(requester);
        when(itemRequestRepository.save(any(ItemRequest.class))).thenAnswer(invocation -> {
            ItemRequest request = invocation.getArgument(0);
            request.setId(5L);
            return request;
        });

        ItemRequestResponseDto response = itemRequestService.createRequest(createDto, 1L);

        assertThat(response.getId()).isEqualTo(5L);
        assertThat(response.getDescription()).isEqualTo(createDto.getDescription());
        assertThat(response.getAnswers()).isEmpty();

        ArgumentCaptor<ItemRequest> requestCaptor = ArgumentCaptor.forClass(ItemRequest.class);
        verify(itemRequestRepository).save(requestCaptor.capture());
        ItemRequest saved = requestCaptor.getValue();
        assertThat(saved.getRequester()).isEqualTo(requester);
        assertThat(saved.getCreated()).isNotNull();
    }

    @Test
    void getUserRequests_returnsSortedRequestsWithAnswers() {
        Item item = new Item();
        item.setId(99L);
        item.setName("Drill");
        item.setOwner(requester);
        item.setRequest(itemRequest);

        when(userService.getUserById(1L)).thenReturn(requester);
        when(itemRequestRepository.findAllByRequester_IdOrderByCreatedDesc(1L))
                .thenReturn(List.of(itemRequest));
        when(itemRepository.findAllByRequest_IdIn(List.of(10L))).thenReturn(List.of(item));

        List<ItemRequestResponseDto> responses = itemRequestService.getUserRequests(1L);

        assertThat(responses).hasSize(1);
        ItemRequestResponseDto response = responses.getFirst();
        assertThat(response.getId()).isEqualTo(10L);
        assertThat(response.getAnswers()).singleElement().satisfies(answer -> {
            assertThat(answer.getId()).isEqualTo(99L);
            assertThat(answer.getOwnerId()).isEqualTo(1L);
            assertThat(answer.getRequestId()).isEqualTo(10L);
        });
    }

    @Test
    void getOtherUsersRequests_usesPaginationAndSorting() {
        User anotherUser = new User(2L, "other@example.com", "Other");
        ItemRequest otherRequest = new ItemRequest();
        otherRequest.setId(11L);
        otherRequest.setDescription("Need ladder");
        otherRequest.setCreated(LocalDateTime.now());
        otherRequest.setRequester(anotherUser);

        when(userService.getUserById(1L)).thenReturn(requester);
        when(itemRequestRepository.findAllByRequester_IdNot(eq(1L), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(otherRequest)));
        when(itemRepository.findAllByRequest_IdIn(List.of(11L))).thenReturn(List.of());

        List<ItemRequestResponseDto> responses = itemRequestService.getOtherUsersRequests(1L, 0, 5);

        assertThat(responses).hasSize(1);
        verify(itemRequestRepository).findAllByRequester_IdNot(eq(1L),
                eq(PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "created"))));
    }

    @Test
    void getRequestById_returnsRequestWithAnswers() {
        Item item = new Item();
        item.setId(99L);
        item.setName("Drill");
        item.setOwner(requester);
        item.setRequest(itemRequest);

        when(userService.getUserById(1L)).thenReturn(requester);
        when(itemRequestRepository.findById(10L)).thenReturn(java.util.Optional.of(itemRequest));
        when(itemRepository.findAllByRequest_IdIn(List.of(10L))).thenReturn(List.of(item));

        ItemRequestResponseDto response = itemRequestService.getRequestById(1L, 10L);

        assertThat(response.getId()).isEqualTo(10L);
        assertThat(response.getAnswers()).hasSize(1);
    }

    @Test
    void getRequestById_throwsIfNotFound() {
        when(userService.getUserById(1L)).thenReturn(requester);
        when(itemRequestRepository.findById(10L)).thenReturn(java.util.Optional.empty());

        assertThatThrownBy(() -> itemRequestService.getRequestById(1L, 10L))
                .isInstanceOf(NotFoundException.class);
    }
}
