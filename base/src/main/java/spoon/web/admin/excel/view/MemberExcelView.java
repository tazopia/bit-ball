package spoon.web.admin.excel.view;

import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractXlsxView;
import spoon.common.utils.DateUtils;
import spoon.common.utils.WebUtils;
import spoon.member.domain.Role;
import spoon.member.entity.Member;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Map;

@Component
public class MemberExcelView extends AbstractXlsxView {

    @Override
    protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Role role = WebUtils.role();

        Iterable<Member> members = (Iterable<Member>) model.get("members");

        DataFormat fmt = workbook.createDataFormat();
        CellStyle textStyle = workbook.createCellStyle();
        textStyle.setDataFormat(fmt.getFormat("@"));

        response.setHeader("Content-Disposition", "attachment; filename=\"member_" + DateUtils.format(new Date(), "yyyyMMdd_HHmmss") + ".xlsx\"");

        Sheet sheet = workbook.createSheet("회원목록");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("등급");
        header.createCell(1).setCellValue("아이디");
        header.createCell(2).setCellValue("닉네임");
        header.createCell(3).setCellValue("전화번호");
        header.createCell(4).setCellValue("총본사");
        header.createCell(5).setCellValue("부본사");
        header.createCell(6).setCellValue("총판");
        header.createCell(7).setCellValue("매장");
        header.createCell(8).setCellValue("도메인");
        header.createCell(9).setCellValue("베팅");
        header.createCell(10).setCellValue("총입금");
        header.createCell(11).setCellValue("총환전");
        header.createCell(12).setCellValue("차액");
        header.createCell(13).setCellValue("머니");
        header.createCell(14).setCellValue("포인트");
        header.createCell(15).setCellValue("가입일");
        header.createCell(16).setCellValue("접속일");
        header.createCell(17).setCellValue("아이피");
        header.createCell(18).setCellValue("탈퇴여부");
        header.createCell(19).setCellValue("환전비번");
        header.createCell(20).setCellValue("메모");

        int rowNum = 1;
        for (Member m : members) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(m.getLevel());
            row.createCell(1).setCellValue(m.getUserid());
            row.createCell(2).setCellValue(m.getNickname());

            // 전화번호는 문자열로 스타일을 지정한다.
            Cell cell3 = row.createCell(3);
            cell3.setCellStyle(textStyle);
            cell3.setCellValue(role == Role.GOD ? m.getPassKey() : m.getPhone());

            row.createCell(4).setCellValue(m.getAgency4());
            row.createCell(5).setCellValue(m.getAgency3());
            row.createCell(6).setCellValue(m.getAgency2());
            row.createCell(7).setCellValue(m.getAgency1());
            row.createCell(8).setCellValue(m.getJoinDomain());
            row.createCell(9).setCellValue(m.getBetCntTotal());
            row.createCell(10).setCellValue(m.getDeposit());
            row.createCell(11).setCellValue(m.getWithdraw());
            row.createCell(12).setCellValue(m.getDeposit() - m.getWithdraw());
            row.createCell(13).setCellValue(m.getMoney());
            row.createCell(14).setCellValue(m.getPoint());
            row.createCell(15).setCellValue(m.getJoinDate() == null ? "-" : DateUtils.format(m.getJoinDate(), "yyyy-MM-dd HH:mm:ss"));
            row.createCell(16).setCellValue(m.getLoginDate() == null ? "-" : DateUtils.format(m.getLoginDate(), "yyyy-MM-dd HH:mm:ss"));
            row.createCell(17).setCellValue(m.getLoginIp());
            row.createCell(18).setCellValue(m.isSecession() ? "탈퇴" : "정상");
            row.createCell(19).setCellValue(m.getBankPassword());
            row.createCell(20).setCellValue(m.getMemo().replaceAll(System.getProperty("line.separator"), "\\n"));
        }
    }
}
