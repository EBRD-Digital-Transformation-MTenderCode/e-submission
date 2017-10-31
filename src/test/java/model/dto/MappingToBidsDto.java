package model.dto;

import com.procurement.submission.model.dto.BidsDto;
import model.AbstractDomainObjectTest;
import org.junit.jupiter.api.Test;

public class MappingToBidsDto extends AbstractDomainObjectTest {

//    @Test
    public void testJsonToBidDtoToJson() {
        compare(BidsDto.class,
            "json/BidsDto.json");
    }
}
