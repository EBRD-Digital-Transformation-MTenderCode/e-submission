package model.dto;

import com.procurement.submission.model.dto.ContractProcessPeriodDto;
import model.AbstractDomainObjectTest;
import org.junit.jupiter.api.Test;

public class MappingToContractProcessPeriodDto extends AbstractDomainObjectTest {

//    @Test
    public void testJsonToContractProcessPeriodDtoToJson() {
        compare(ContractProcessPeriodDto.class,
            "json/ContractProcessPeriodDto.json");
    }
}
