package az.m10.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class VolunteerProfileImageDto {
    private MultipartFile profileImage;
}
