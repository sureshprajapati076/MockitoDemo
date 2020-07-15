package com.example.demo;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.example.demo.domain.Product;
import com.example.demo.domain.Vendor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;




@RunWith(SpringRunner.class)
@SpringBootTest
class TestForControllerHittingDataBase {



    //THIS       IS   FOR TESTING CONTROLLER THROUGH MOCKMVC
	ObjectMapper om = new ObjectMapper();


    private MockMvc mockMvc;


    @Autowired
    private WebApplicationContext context;

    @Before
    private void setUp(){
        //   mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

    }

    @Test
    public void addProductTest() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
           Vendor vendor = new Vendor(1L, "XYZ", "NEPAL KTM", "2092092090");
        
             Product p=new Product(40L, "Head Phone4", "Good and Nice1", LocalDate.of(2020, 9, 9), new BigDecimal(34.45), 23, vendor);

     /*   String jsonString="{\n" +
                "  \"name\":\"Product D\",\n" +
                "  \"description\":\"nice\",\n" +
                "  \"expiryDate\":\"2020-09-07\",\n" +
                "  \"unitPrice\":\"10\",\n" +
                "  \"quantity\":50,\n" +
                "  \"vendor\":{\n" +
                "    \"id\":\"1\"\n" +
                "  }\n" +
                "\n" +
                "\n" +
                "}";
                */
             

     
        
       
        om.registerModule(new JavaTimeModule());
        om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        
      String jsonString=om.writeValueAsString(p);
       
        
        
        


        mockMvc.perform(post("/addproduct")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonString))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", Matchers.is("Head Phone4")))
                .andExpect(jsonPath("$.description", Matchers.is("Good and Nice1")))
                .andExpect(jsonPath("$.*", Matchers.hasSize(7)));
    }

    //Testing is done for controller only here. it is hitting database
    @Test
    public void getProductByIdTest() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        mockMvc.perform(get("/getbyid/1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.expiryDate", Matchers.is(LocalDate.of(2020,9,7).toString())))
                .andExpect(jsonPath("$.unitPrice", Matchers.is(10.00)))
                .andExpect(jsonPath("$.*", Matchers.hasSize(7)));
    }







}
