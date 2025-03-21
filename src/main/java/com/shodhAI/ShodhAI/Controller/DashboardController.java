package com.shodhAI.ShodhAI.Controller;
import com.shodhAI.ShodhAI.Dto.AssignmentStatisticsDto;
import com.shodhAI.ShodhAI.Service.AssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("dashboard")
public class DashboardController {

    @Autowired
    private AssignmentService assignmentService;

    @GetMapping("/faculty/{facultyId}/assignment/{assignmentId}")
    public ResponseEntity<Map<String, Object>> getAssignmentDashboard(
            @PathVariable Long facultyId,
            @PathVariable Long assignmentId) {
        try {
            // Get assignment statistics
            AssignmentStatisticsDto statistics = assignmentService.getAssignmentCompletionStatistics(assignmentId, facultyId);
            
            // Prepare dashboard data
            Map<String, Object> dashboardData = new HashMap<>();
            dashboardData.put("statistics", statistics);
            
            // Additional visualization data
            Map<String, Object> visualizationData = new HashMap<>();
            
            // Completion rate pie chart data
            Map<String, Object> pieChartData = new HashMap<>();
            pieChartData.put("completed", statistics.getCompletedCount());
            pieChartData.put("pending", statistics.getTotalStudents() - statistics.getCompletedCount());
            visualizationData.put("pieChart", pieChartData);
            
            // Progress over time data (using completion by date)
            visualizationData.put("timelineChart", statistics.getCompletionByDate());
            
            // Performance ranking data (top performers based on scores)
            visualizationData.put("rankingData", statistics.getStudentCompletionList().stream()
                    .filter(student -> student.getScore() != null)
                    .sorted((s1, s2) -> s2.getScore().compareTo(s1.getScore()))
                    .limit(5)
                    .toArray());
            
            dashboardData.put("visualizationData", visualizationData);
            
            return new ResponseEntity<>(dashboardData, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}