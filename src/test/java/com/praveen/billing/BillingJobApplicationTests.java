package com.praveen.billing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@SpringBatchTest
class BillingJobApplicationTests {

  @Autowired private JobLauncherTestUtils jobLauncherTestUtils;

  @Autowired private JobRepositoryTestUtils jobRepositoryTestUtils;

  @BeforeEach
  public void setUp() {
    this.jobRepositoryTestUtils.removeJobExecutions();
  }

  @Test
  void testJobExecution() throws Exception {
    // given
    JobParameters jobParameters =
        new JobParametersBuilder()
            .addString("input.file", "src/main/resources/billing-2023-01.csv")
            .toJobParameters();

    // when
    JobExecution jobExecution = this.jobLauncherTestUtils.launchJob(jobParameters);
    Path billingReport = Paths.get("staging", "billing-report-2023-01.csv");
    final var lines = Files.lines(billingReport).count();

    // then
    assertThat(jobExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
    assertThat(Paths.get("staging", "billing-2023-01.csv")).exists();
    assertThat(billingReport).exists();
    assertThat(lines).isEqualTo(781);
  }
}
