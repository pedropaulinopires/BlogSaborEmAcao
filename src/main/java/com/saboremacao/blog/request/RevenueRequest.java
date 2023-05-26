package com.saboremacao.blog.request;

import com.saboremacao.blog.domain.Login;
import com.saboremacao.blog.domain.Revenue;
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
public class RevenueRequest {

    @NotEmpty(message = "Campo nome é obrigatório!")
    private String name;

    @NotEmpty(message = "Campo de detalhes é obrigatório!")
    private String details;

    @NotEmpty(message = "Campo de ingredientes é obrigatório!")
    private String ingredients;

    @NotEmpty(message = "Campo de preparação é obrigatório!")
    private String preparation;

    private Section section;

    private String image;

    private String datePublication;

    private Login author;

    public Revenue build() {
        return new Revenue().builder().name(this.name).details(this.details).ingredients(this.ingredients).preparation(this.preparation).section(this.section).datePublication(this.datePublication).author(this.author).image(this.image).build();
    }

    public Revenue build(RevenueRequest revenueRequest) {
        return new Revenue().builder().name(revenueRequest.getName()).details(revenueRequest.getDetails()).ingredients(revenueRequest.getIngredients()).preparation(revenueRequest.getPreparation()).section(revenueRequest.getSection()).datePublication(revenueRequest.getDatePublication()).author(revenueRequest.getAuthor()).image(revenueRequest.getImage()).build();
    }

    public Revenue build(RevenueRequest revenueRequest, UUID id) {
        return new Revenue().builder().id(id).name(revenueRequest.getName()).details(revenueRequest.getDetails()).ingredients(revenueRequest.getIngredients()).preparation(revenueRequest.getPreparation()).section(revenueRequest.getSection()).author(revenueRequest.getAuthor()).datePublication(revenueRequest.getDatePublication()).image(revenueRequest.getImage()).build();
    }

    public void fromRevenue(Revenue revenue) {
        this.name = revenue.getName();
        this.details = revenue.getDetails();
        this.ingredients = revenue.getIngredients();
        this.preparation = revenue.getPreparation();
        this.section = revenue.getSection();
        this.image = revenue.getImage();
        this.datePublication = revenue.getDatePublication();
        this.author = revenue.getAuthor();
    }
}
