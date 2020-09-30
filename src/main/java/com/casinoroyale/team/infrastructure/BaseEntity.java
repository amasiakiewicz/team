package com.casinoroyale.team.infrastructure;

import static com.casinoroyale.team.TeamApplication.DEFAULT_ZONE_OFFSET;
import static javax.persistence.AccessType.FIELD;
import static lombok.AccessLevel.PUBLIC;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

import javax.persistence.Access;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

import lombok.Getter;
import lombok.NoArgsConstructor;

@MappedSuperclass
@Access(FIELD)
@NoArgsConstructor(access = PUBLIC)
public abstract class BaseEntity {

    @Id
    @Getter
    private UUID id = UUID.randomUUID();

    @Version
    private Integer version;

    private final OffsetDateTime createdDateTime = OffsetDateTime.now(DEFAULT_ZONE_OFFSET);

    public BaseEntity(final UUID id) {
        this.id = id;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final BaseEntity that = (BaseEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
