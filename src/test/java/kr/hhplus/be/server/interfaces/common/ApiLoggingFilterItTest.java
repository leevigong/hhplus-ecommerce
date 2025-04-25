package kr.hhplus.be.server.interfaces.common;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(OutputCaptureExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class ApiLoggingFilterItTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void API_로깅이_출력된다(CapturedOutput output) throws Exception {
        // when
        mockMvc.perform(get("/api/v1/balances/1"))
                .andExpect(status().isOk());

        // then
        String logs = output.toString();
        assertThat(logs)
                .contains("REQUEST: GET /api/v1/balances/1")
                .contains("RESPONSE: GET /api/v1/balances/1 STATUS=200");
    }
}
