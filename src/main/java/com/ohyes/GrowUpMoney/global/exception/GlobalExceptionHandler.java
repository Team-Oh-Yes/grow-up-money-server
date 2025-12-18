package com.ohyes.GrowUpMoney.global.exception;

import com.ohyes.GrowUpMoney.domain.auth.exception.AccountSuspendedException;
import com.ohyes.GrowUpMoney.domain.auth.exception.AccountWithdrawnException;
import com.ohyes.GrowUpMoney.domain.auth.exception.PasswordException;
import com.ohyes.GrowUpMoney.domain.nft.exception.NftException;
import com.ohyes.GrowUpMoney.domain.quiz.exception.QuizException;
import com.ohyes.GrowUpMoney.domain.roadmap.exception.RoadmapException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ProblemDetail> handleBaseException(BaseException ex) {

        HttpStatus status = ex.getStatus();

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                status,
                ex.getMessage()
        );

        return ResponseEntity.status(status).body(problemDetail);
    }

    @ExceptionHandler(AccountSuspendedException.class)
    public ResponseEntity<ProblemDetail> handleAccountSuspended(AccountSuspendedException ex) {
        HttpStatus status = HttpStatus.FORBIDDEN;
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                status,
                ex.getMessage()
        );
        problemDetail.setTitle("Account Suspended");
        problemDetail.setProperty("error_code", "E403_ACCOUNT_SUSPENDED");
        return ResponseEntity.status(status).body(problemDetail);
    }

    @ExceptionHandler(AccountWithdrawnException.class)
    public ResponseEntity<ProblemDetail> handleAccountWithdrawn(AccountWithdrawnException ex) {
        HttpStatus status = HttpStatus.FORBIDDEN;
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                status,
                ex.getMessage()
        );
        problemDetail.setTitle("Account Withdrawn");
        problemDetail.setProperty("error_code", "E403_ACCOUNT_WITHDRAWN");
        return ResponseEntity.status(status).body(problemDetail);
    }

    // QuizException 처리
    @ExceptionHandler(QuizException.class)
    public ResponseEntity<ProblemDetail> handleQuizException(QuizException ex) {
        HttpStatus status = ex.getStatus();

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                status,
                ex.getMessage()
        );
        problemDetail.setTitle("Quiz Error");
        problemDetail.setProperty("error_code", "E" + status.value() + "_QUIZ_ERROR");

        return ResponseEntity.status(status).body(problemDetail);
    }

    // Security 예외 처리
    @ExceptionHandler({BadCredentialsException.class, UsernameNotFoundException.class})
    public ResponseEntity<ProblemDetail> handleAuthExceptions(Exception ex) {

        HttpStatus status = HttpStatus.UNAUTHORIZED;

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                status,
                "아이디 또는 비밀번호가 일치하지 않습니다."
        );

        return ResponseEntity.status(status).body(problemDetail);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidationExceptions(MethodArgumentNotValidException ex) {

        HttpStatus status = HttpStatus.BAD_REQUEST;

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                status,
                "요청 본문의 유효성 검사에 실패했습니다."
        );
        problemDetail.setTitle("Validation Failed");

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });
        problemDetail.setProperty("errors", errors);
        problemDetail.setProperty("error_code", "E400_VALIDATION_FAILED");

        return ResponseEntity.status(status).body(problemDetail);
    }

    // IllegalArgumentException 처리 (사용자 찾을 수 없음 등)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetail> handleIllegalArgument(IllegalArgumentException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                status,
                ex.getMessage()
        );
        problemDetail.setTitle("Bad Request");
        problemDetail.setProperty("error_code", "E400_BAD_REQUEST");

        return ResponseEntity.status(status).body(problemDetail);
    }

    // IllegalStateException 처리 (하트 부족, 포인트 부족 등)
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ProblemDetail> handleIllegalState(IllegalStateException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                status,
                ex.getMessage()
        );
        problemDetail.setTitle("Bad Request");
        problemDetail.setProperty("error_code", "E400_ILLEGAL_STATE");

        return ResponseEntity.status(status).body(problemDetail);
    }

    @ExceptionHandler(PasswordException.class)
    public ResponseEntity<ProblemDetail> handlePasswordException(PasswordException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                status,
                ex.getMessage()
        );
        problemDetail.setTitle("Password Error");
        problemDetail.setProperty("error_code", "E400_PASSWORD_ERROR");

        return ResponseEntity.status(status).body(problemDetail);
    }

    // ===== NFT 예외 처리 (추가) =====

    // NFT 리소스를 찾을 수 없음 (404)
    @ExceptionHandler({
            NftException.NftCollectionNotFoundException.class,
            NftException.NftTokenNotFoundException.class,
            NftException.TradeNotFoundException.class,
            NftException.ThemeRewardNotFoundException.class
    })
    public ResponseEntity<ProblemDetail> handleNftNotFound(RuntimeException ex) {
        HttpStatus status = HttpStatus.NOT_FOUND;

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                status,
                ex.getMessage()
        );
        problemDetail.setTitle("NFT Resource Not Found");
        problemDetail.setProperty("error_code", "E404_NFT_NOT_FOUND");

        return ResponseEntity.status(status).body(problemDetail);
    }

    // NFT 권한 오류 (403)
    @ExceptionHandler({
            NftException.NotNftOwnerException.class,
            NftException.UnauthorizedTradeAccessException.class,
            NftException.ThemeNotCompletedException.class
    })
    public ResponseEntity<ProblemDetail> handleNftUnauthorized(RuntimeException ex) {
        HttpStatus status = HttpStatus.FORBIDDEN;

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                status,
                ex.getMessage()
        );
        problemDetail.setTitle("NFT Unauthorized Access");
        problemDetail.setProperty("error_code", "E403_NFT_UNAUTHORIZED");

        return ResponseEntity.status(status).body(problemDetail);
    }

    // NFT 비즈니스 로직 오류 (400)
    @ExceptionHandler({
            NftException.MaxSupplyExceededException.class,
            NftException.CollectionNftNotTradeableException.class,
            NftException.AlreadyOnSaleException.class,
            NftException.NotOnSaleException.class,
            NftException.InsufficientPointException.class,
            NftException.PriceOutOfRangeException.class,
            NftException.CannotBuyOwnNftException.class,
            NftException.InvalidTradeStatusException.class,
            NftException.DuplicateRewardException.class,
            NftException.CollectionNotInThemeException.class
    })
    public ResponseEntity<ProblemDetail> handleNftBusinessError(RuntimeException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                status,
                ex.getMessage()
        );
        problemDetail.setTitle("NFT Business Logic Error");
        problemDetail.setProperty("error_code", "E400_NFT_BUSINESS_ERROR");

        return ResponseEntity.status(status).body(problemDetail);
    }

}