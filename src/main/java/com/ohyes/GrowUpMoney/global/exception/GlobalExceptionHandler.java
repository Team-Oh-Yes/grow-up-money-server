package com.ohyes.GrowUpMoney.global.exception;

import com.ohyes.GrowUpMoney.domain.auth.exception.AccountSuspendedException;
import com.ohyes.GrowUpMoney.domain.auth.exception.AccountWithdrawnException;
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

    //Security 예외 처리
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

    // quizexception
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
}
