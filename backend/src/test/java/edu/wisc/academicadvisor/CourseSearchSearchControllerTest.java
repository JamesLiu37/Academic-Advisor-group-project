package edu.wisc.academicadvisor;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class CourseSearchSearchControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void noParamCoursesShouldReturnResults() throws Exception {

        this.mockMvc.perform(get("/courses")).andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isNotEmpty());
    }

    @Test
    public void invalidParamCoursesShouldReturnNoResults() throws Exception {

        this.mockMvc.perform(get("/courses").param("breadth", "fake"))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());
    }
}