package ru.practicum.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.item.Item;
import ru.practicum.item.ItemRepository;
import ru.practicum.request.dto.ItemRequestCreateDto;
import ru.practicum.request.dto.ItemRequestResponseDto;
import ru.practicum.user.User;
import ru.practicum.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ItemRequestServiceIntegrationTest {

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void createRequestPersistsRequestAndReturnsDto() {
        User requester = userRepository.save(new User(null, "requester@email.com", "Requester"));

        ItemRequestCreateDto createDto = new ItemRequestCreateDto("Need a drill");
        ItemRequestResponseDto response = itemRequestService.createRequest(createDto, requester.getId());

        assertThat(response.getId()).isNotNull();
        assertThat(response.getDescription()).isEqualTo("Need a drill");
        assertThat(response.getItems()).isEmpty();
        assertThat(itemRequestRepository.findById(response.getId()))
                .get()
                .extracting(ItemRequest::getRequester)
                .extracting(User::getId)
                .isEqualTo(requester.getId());
    }

    @Test
    void getUserRequestsReturnsRequestsWithItems() {
        User requester = userRepository.save(new User(null, "requester2@email.com", "Requester 2"));
        ItemRequest olderRequest = new ItemRequest();
        olderRequest.setRequester(requester);
        olderRequest.setDescription("Old request");
        olderRequest.setCreated(LocalDateTime.now().minusDays(1));
        olderRequest = itemRequestRepository.save(olderRequest);

        ItemRequest newerRequest = new ItemRequest();
        newerRequest.setRequester(requester);
        newerRequest.setDescription("New request");
        newerRequest.setCreated(LocalDateTime.now());
        newerRequest = itemRequestRepository.save(newerRequest);

        Item item = new Item();
        item.setName("Drill");
        item.setDescription("Cordless");
        item.setAvailable(true);
        item.setOwner(requester);
        item.setRequest(newerRequest);
        itemRepository.save(item);

        List<ItemRequestResponseDto> responses = itemRequestService.getUserRequests(requester.getId());

        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getId()).isEqualTo(newerRequest.getId());
        assertThat(responses.get(0).getItems())
                .hasSize(1)
                .first()
                .extracting("name")
                .isEqualTo("Drill");
    }

    @Test
    void getOtherUsersRequestsReturnsPagedRequests() {
        User requester = userRepository.save(new User(null, "requester3@email.com", "Requester 3"));
        User otherUser = userRepository.save(new User(null, "owner@email.com", "Owner"));

        ItemRequest otherRequest = new ItemRequest();
        otherRequest.setRequester(otherUser);
        otherRequest.setDescription("Need a bike");
        otherRequest.setCreated(LocalDateTime.now());
        otherRequest = itemRequestRepository.save(otherRequest);

        ItemRequest ownRequest = new ItemRequest();
        ownRequest.setRequester(requester);
        ownRequest.setDescription("Own request");
        ownRequest.setCreated(LocalDateTime.now());
        itemRequestRepository.save(ownRequest);

        List<ItemRequestResponseDto> responses = itemRequestService.getOtherUsersRequests(requester.getId(), 0, 10);

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getId()).isEqualTo(otherRequest.getId());
        assertThat(responses.get(0).getDescription()).isEqualTo("Need a bike");
    }

    @Test
    void getRequestByIdReturnsRequestWithItems() {
        User requester = userRepository.save(new User(null, "requester4@email.com", "Requester 4"));

        ItemRequest request = new ItemRequest();
        request.setRequester(requester);
        request.setDescription("Need a ladder");
        request.setCreated(LocalDateTime.now());
        request = itemRequestRepository.save(request);

        Item item = new Item();
        item.setName("Ladder");
        item.setDescription("Foldable");
        item.setAvailable(true);
        item.setOwner(requester);
        item.setRequest(request);
        itemRepository.save(item);

        ItemRequestResponseDto response = itemRequestService.getRequestById(requester.getId(), request.getId());

        assertThat(response.getId()).isEqualTo(request.getId());
        assertThat(response.getItems())
                .hasSize(1)
                .first()
                .extracting("name")
                .isEqualTo("Ladder");
    }
}
