package in.nvijaykarthik.cmc.dao.entity;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode
public class Filter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "xpath")
    private String xpath;

    @Column(name = "type")
    private String type;

    @Column(name = "status")
    private String status;

    @Column(name = "created_date")
    private LocalDate createdDate;

    @OneToMany(mappedBy = "filter")
    private Set<FilterCustomerXref> filterCustomers = new HashSet<>();

}