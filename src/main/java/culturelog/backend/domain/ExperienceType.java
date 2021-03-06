package culturelog.backend.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

/**
 * @author Jan Venstermans
 */
@Entity
@Table(name = "experience_type", uniqueConstraints = {
    @UniqueConstraint(name = "experienceType_nameUser_unique", columnNames = {"name", "userId"})
})
public class ExperienceType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Name of the experienceType. Is used as a key.
     */
    @NotNull
    private String name;

    /**
     * Description or title of the experienceType.
     */
    private String description;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return String.format(
                "ExperienceType[id=%s, name='%s', description='%s', global='%s']",
                id, name, description, user == null);
    }
}
