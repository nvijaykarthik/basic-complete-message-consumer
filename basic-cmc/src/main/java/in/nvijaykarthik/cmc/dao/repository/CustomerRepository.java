package in.nvijaykarthik.cmc.dao.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import in.nvijaykarthik.cmc.dao.CustomerFilterDTO;
import in.nvijaykarthik.cmc.dao.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    String Query = """
        SELECT DISTINCT new in.nvijaykarthik.cmc.dao.CustomerFilterDTO(c.id, c.name, f.xpath)
        FROM Customer c 
        JOIN FilterCustomerXref fcx ON c.id = fcx.customer.id 
        JOIN Filter f ON fcx.filter.id = f.id 
        WHERE f.status = 'active' AND f.type = :filterType
        """;

    @Query(value = Query)
    List<CustomerFilterDTO> findCustomersWithActiveFilterAndXpathByType(@Param("filterType") String filterType);
}