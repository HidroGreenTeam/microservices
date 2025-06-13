package com.hidrogreen.user_service.profiles.domain.model.aggregates;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.hidrogreen.user_service.profiles.domain.model.commands.CreateFarmerCommand;
import com.hidrogreen.user_service.profiles.domain.model.entities.FarmerImage;
import com.hidrogreen.user_service.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;

/**
 * Farmer
 *
 * <p>
 *     This class represents a Farmer.
 * </p>
 *
 */

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Farmer extends AuditableAbstractAggregateRoot<Farmer> {

    private String username;

    private String email;

    private String phoneNumber;

    private String password;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "farmer_image_id", referencedColumnName = "id")
    private FarmerImage farmerImage;

    public Farmer(CreateFarmerCommand command) {
        this.username = command.username();
        this.email = command.email();
        this.phoneNumber = command.phoneNumber();
        this.password = command.password();
    }

}
