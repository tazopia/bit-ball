package spoon.banking.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import spoon.banking.domain.BankingCode;
import spoon.banking.domain.BankingDto;
import spoon.banking.entity.Banking;

public interface BankingListService {

    /**
     * 사용자 충전/환전 신청 리스트
     */
    Iterable<Banking> list(String userid, BankingCode bankingCode);

    /**
     * 관리자 충전/환전 리스트 페이지
     */
    Page<Banking> page(BankingDto.Command command, Pageable pageable);

    /**
     * 관리자 기간별 충환전 요약
     */
    BankingDto.Money bankingTotal(BankingDto.Date command);


    /**
     * 유저별 충환전 리스트 페이지
     */
    Page<Banking> bankingPage(String userid, Pageable pageable);

    /**
     * 총판, 대리점 충환전 리스트 페이지
     */
    Page<Banking> bankingPage(BankingDto.Seller command, Pageable pageable);
}
