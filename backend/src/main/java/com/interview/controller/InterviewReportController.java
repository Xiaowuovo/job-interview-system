package com.interview.controller;

import com.interview.common.Result;
import com.interview.entity.InterviewReport;
import com.interview.service.InterviewReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/interview/reports")
@RequiredArgsConstructor
public class InterviewReportController {

    private final InterviewReportService reportService;

    @PostMapping("/generate/{sessionId}")
    public Result<InterviewReport> generateReport(@PathVariable Long sessionId) {
        InterviewReport report = reportService.generateReport(sessionId);
        return Result.success(report);
    }

    @GetMapping("/session/{sessionId}")
    public Result<InterviewReport> getReport(@PathVariable Long sessionId) {
        InterviewReport report = reportService.getReportBySessionId(sessionId);
        return Result.success(report);
    }
}
