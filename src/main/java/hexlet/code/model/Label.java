package hexlet.code.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "labels")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class Label implements BaseEntity {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @EqualsAndHashCode.Include
    private long id;

    @Column(unique = true, nullable = false)
    @Size(min = 3, max = 1000)
    @EqualsAndHashCode.Include
    private String name;

    @CreatedDate
    private LocalDate createdAt;

    @ManyToMany(mappedBy = "labels", fetch = FetchType.LAZY)
    private Set<Task> tasks = new HashSet<>();

    public void addTask(Task task) {
        tasks.add(task);
    }

    public void removeTask(Task task) {
        tasks.remove(task);
    }
}
