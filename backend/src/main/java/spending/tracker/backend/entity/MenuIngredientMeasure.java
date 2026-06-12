package spending.tracker.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "menu_ingredient_measures")
@IdClass(MenuIngredientMeasureId.class)
@Getter
@Setter
public class MenuIngredientMeasure {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_id", nullable = false)
    private MenuIngredient ingredient;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "measure_id", nullable = false)
    private MenuMeasure measure;

    @Column(name = "base_unit_coefficient", nullable = false, precision = 10, scale = 4)
    private BigDecimal baseUnitCoefficient;
}