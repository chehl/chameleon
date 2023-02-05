package cde.chameleon.locations.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "locations")
public class LocationEntity {

    @ToString.Include
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    // version number for optimistic locking
    @Version
    @Column(name = "opt_lock", nullable = false)
    private Long optLock;

    @ToString.Include
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "latitude", nullable = false)
    private BigDecimal latitude;

    @Column(name = "longitude", nullable = false)
    private BigDecimal longitude;

    public BigDecimal getLatitude() {
        return latitude.stripTrailingZeros(); // Database results may contain trailing zeros!
    }

    public BigDecimal getLongitude() {
        return longitude.stripTrailingZeros(); // Database results may contain trailing zeros!
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Generated
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        LocationEntity that = (LocationEntity) o;
        return id != null && Objects.equals(id, that.id);
    }
}
