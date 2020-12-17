package spoon.mapper;

import org.apache.ibatis.annotations.Param;
import spoon.customer.domain.MemoDto;

public interface MemoMapper {

    void addMemo(@Param("add") MemoDto.Add add);

    void deleteMemo(@Param("ids") Long[] memoIds);
}
