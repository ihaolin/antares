package me.hao0.antares.store.service;

import me.hao0.antares.common.dto.DependenceJob;
import me.hao0.antares.common.dto.JobDetail;
import me.hao0.antares.common.dto.JobInstanceDto;
import me.hao0.antares.common.model.Job;
import me.hao0.antares.common.model.JobDependence;
import me.hao0.antares.common.model.JobInstance;
import me.hao0.antares.common.model.enums.JobStatus;
import me.hao0.antares.store.BaseTest;
import me.hao0.antares.store.util.Page;
import me.hao0.antares.store.util.Response;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.Assert.*;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public class JobServiceTest extends BaseTest {

    @Autowired
    private JobService jobService;

    @Test
    public void testSaveJobDetail(){
        JobDetail detail = new JobDetail();
        detail.setJob(mockJob());
        detail.setConfig(mockJobConfig(1, "0=1"));

        assertTrue(jobService.saveJobDetail(detail).isSuccess());
    }

    @Test
    public void testSaveJobDetails(){

        int count = 22;

        for (int i=0; i<count; i++){
            JobDetail detail = new JobDetail();
            Job j = mockJob();
            j.setClazz(j.getClazz() + i);
            j.setStatus(JobStatus.ENABLE.value());
            detail.setJob(j);
            detail.setConfig(mockJobConfig(1, "0=1"));
            assertTrue(jobService.saveJobDetail(detail).isSuccess());
        }
    }

    @Test
    public void testFindJobById(){
        Response<Job> findResp = jobService.findJobById(2L);
        assertTrue(findResp.isSuccess());
        assertNotNull(findResp.getData());

        findResp = jobService.findJobById(404L);
        assertTrue(findResp.isSuccess());
        assertNull(findResp.getData());
    }

    @Test
    public void testFindJobDetailById(){
        Response<JobDetail> findResp = jobService.findJobDetailById(2L);
        assertTrue(findResp.isSuccess());
        assertNotNull(findResp.getData());

        findResp = jobService.findJobDetailById(404L);
        assertTrue(findResp.isSuccess());
        assertNull(findResp.getData());
    }

    @Test
    public void testPagingJob(){
        Response<Page<Job>> pagingResp = jobService.pagingJob(1L, "", 1, 10);
        assertTrue(pagingResp.isSuccess());
        assertNotNull(pagingResp.getData());
        assertEquals(10, pagingResp.getData().getData().size());
        assertEquals(22, pagingResp.getData().getTotal().intValue());

        pagingResp = jobService.pagingJob(1L, "", 3, 10);
        assertTrue(pagingResp.isSuccess());
        assertNotNull(pagingResp.getData());
        assertEquals(2, pagingResp.getData().getData().size());
        assertEquals(22, pagingResp.getData().getTotal().intValue());

        pagingResp = jobService.pagingJob(1L, "", 1, 5);
        assertTrue(pagingResp.isSuccess());
        assertNotNull(pagingResp.getData());
        assertEquals(5, pagingResp.getData().getData().size());
        assertEquals(22, pagingResp.getData().getTotal().intValue());
    }

    @Test
    public void testSaveJobInstance(){
        JobInstance instance = mockJobInstance(1L);
        Response<Boolean> saveResp = jobService.createJobInstance(instance);
        assertTrue(saveResp.isSuccess());
        assertTrue(saveResp.getData());
    }

    @Test
    public void testPagingInstance(){
        Response<Page<JobInstanceDto>> pagingResp = jobService.pagingJobInstance(1L, "", 1, 10);
        assertTrue(pagingResp.isSuccess());
        assertNotNull(pagingResp.getData());
        assertEquals(1, pagingResp.getData().getData().size());
        assertEquals(1, pagingResp.getData().getTotal().intValue());

        pagingResp = jobService.pagingJobInstance(1L, "", 2, 10);
        assertTrue(pagingResp.isSuccess());
        assertNotNull(pagingResp.getData());
        assertEquals(0, pagingResp.getData().getData().size());
        assertEquals(1, pagingResp.getData().getTotal().intValue());
    }

    @Test
    public void testDeleteJob(){
        Response<Boolean> deleteResp = jobService.deleteJob(1L);
        assertTrue(deleteResp.isSuccess());
        assertTrue(deleteResp.getData());
    }

    @Test
    public void testAddJobDependence(){
        JobDependence jd = new JobDependence();
        jd.setJobId(2L);
        jd.setNextJobId(5L);
        Response<Boolean> addResp = jobService.addJobDependence(jd);
        assertTrue(addResp.isSuccess());
        assertTrue(addResp.getData());
    }

    @Test
    public void testPagingJobDependence(){
        Response<Page<DependenceJob>> pagingResp = jobService.pagingNextJobs(1L, 1, 10);
        assertTrue(pagingResp.isSuccess());
        assertEquals(0, pagingResp.getData().getData().size());

        pagingResp = jobService.pagingNextJobs(2L, 1, 10);
        assertTrue(pagingResp.isSuccess());
        assertEquals(1, pagingResp.getData().getData().size());
    }

    @Test
    public void testDeleteNextJob(){

        Response<Boolean> deleteResp = jobService.deleteNextJob(1L, 404L);
        assertTrue(deleteResp.isSuccess());
        assertFalse(deleteResp.getData());

        deleteResp = jobService.deleteNextJob(1L, 2L);
        assertTrue(deleteResp.isSuccess());
        assertFalse(deleteResp.getData());

        deleteResp = jobService.deleteNextJob(2L, 5L);
        assertTrue(deleteResp.isSuccess());
        assertTrue(deleteResp.getData());
    }

    @Test
    public void testDeleteNextJobs(){
        Response<Boolean> deleteResp = jobService.deleteNextJobs(2L);
        assertTrue(deleteResp.isSuccess());
        assertTrue(deleteResp.getData());
    }
}
