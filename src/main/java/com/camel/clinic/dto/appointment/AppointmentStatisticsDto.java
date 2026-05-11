package com.camel.clinic.dto.appointment;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AppointmentStatisticsDto {
    private long todayCount;
    private long upcomingCount;

    private long pendingCount;
    private long confirmedCount;
    private long checkedInCount;
    private long inProgressCount;
    private long completedCount;
    private long cancelledCount;
    private long noShowCount;
}