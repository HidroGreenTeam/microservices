package com.hidrogreen.user_service.profiles.domain.model.aggregates;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.hidrogreen.user_service.profiles.domain.model.commands.CreateFarmerCommand;
import com.hidrogreen.user_service.profiles.domain.model.entities.FarmerImage;
import com.hidrogreen.user_service.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Farmer extends AuditableAbstractAggregateRoot<Farmer> {

    @Column(unique = true, nullable = false)
    private Long userId;

    private String fullName;

    private String phoneNumber;

    private String address;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "farmer_image_id", referencedColumnName = "id")
    private FarmerImage farmerImage;

    public Farmer(CreateFarmerCommand command) {
        this.userId = command.userId();
        this.fullName = command.fullName();
        this.phoneNumber = command.phoneNumber();
        this.address = command.address();
    }
}
