package com.saboremacao.blog.request;

import com.saboremacao.blog.domain.Section;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class SectionRequest {

    @NotEmpty(message = "Campo nome é obrigatório!")
    private String name;

    public Section build() {
        return new Section().builder()
                .name(this.name)
                .build();
    }

    public Section build(SectionRequest sectionRequest, UUID id) {
        return new Section().builder()
                .id(id)
                .name(sectionRequest.getName())
                .build();
    }

    public void fromSection(Section section) {
        this.name = section.getName();
    }
}
