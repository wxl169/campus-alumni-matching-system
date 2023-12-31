package org.wxl.alumniMatching.domain.vo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author 16956
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageVO {
    private List rows;

    private Long total;

}
