<<<<<<< Updated upstream:src/main/java/com/umust/dobonglife/domain/course/presentation/dto/response/CourseSummaryResponse.java
package com.umust.dobonglife.domain.course.presentation.dto.response;
=======
package com.umust.dobonglife.domain.course.controller.dto.response;

>>>>>>> Stashed changes:src/main/java/com/umust/dobonglife/domain/course/controller/dto/response/CourseSummaryResponse.java
import com.umust.dobonglife.domain.course.domain.constant.CourseLevel;
import com.umust.dobonglife.domain.course.domain.entity.Course;

import java.util.List;

public record CourseSummaryResponse(
        Long id,
        List<String> imageUrls,
        String title,
        String subTitle,
        List<String> tags,
        CourseLevel level
) {
    public static CourseSummaryResponse from(Course course) {
        return new CourseSummaryResponse(
                course.getId(),
                course.getImageUrls(),
                course.getTitle(),
                course.getSubTitle(),
                course.getTags(),
                course.getLevel()
        );
    }
}

