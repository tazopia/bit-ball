package spoon.mapper;

import org.apache.ibatis.annotations.Param;
import spoon.member.domain.UserChange;

import java.util.List;

public interface ChangeMapper {

    void addMember(@Param("list") List<UserChange> list);

}
