package ru.practicum.review;

import lombok.NoArgsConstructor;
import ru.practicum.user.User;
import ru.practicum.user.UserDto;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class ReviewMapper {

    public static ReviewDto mapToReviewDto(Review review) {
        return new ReviewDto(review.getId(), review.getUserId(), review.getItemId(), review.getContent());
    }

    public static List<ReviewDto> mapToReviewDto(Iterable<Review> reviews) {
        List<ReviewDto> reviewDtos = new ArrayList<>();
        reviews.forEach(review -> reviewDtos.add(mapToReviewDto(review)));

        return reviewDtos;
    }

    public static Review mapToNewReview(ReviewDto reviewDto) {
        Review review = new Review();
        review.setId(reviewDto.getId());
        review.setUserId(reviewDto.getUserId());
        review.setItemId(reviewDto.getItemId());
        review.setContent(reviewDto.getContent());

        return review;
    }
}
