import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import src.Application;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
class ApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Test
    public void helloTest() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
        mockMvc.perform(MockMvcRequestBuilders.get("/hello"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Hello, Spring Boot!"));
    }

    @Test
    public void register() throws Exception {
        // setting MockMvc
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    
        mockMvc.perform(MockMvcRequestBuilders.post("/users/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"John Doe\", \"password\": \"securePassword123\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("John Doe"))
                .andDo(print());
    
        mockMvc.perform(MockMvcRequestBuilders.post("/users/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"John Doe\", \"password\": \"securePassword123\"}"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andDo(print());
    }
    
    @Test
    public void createSystemAdminTest() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.post("/users/createSystemAdmin")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Admin User\", \"password\": \"adminPassword123\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Admin User"))
                .andDo(print());
    }
    
    @Test
    public void createBookAdminTest() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.post("/users/createBookAdmin")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Book Admin\", \"password\": \"bookAdminPassword123\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Book Admin"))
                .andDo(print());
    }
    
    @Test
    public void createNormalUserTest() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.post("/users/createNormalUser")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Other User\", \"password\": \"normalUserPassword123\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Other User"))
                .andDo(print());
    }
    

}
