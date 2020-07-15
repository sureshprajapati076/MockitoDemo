package com.example.demo;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.example.demo.domain.Product;
import com.example.demo.domain.Vendor;
import com.example.demo.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;


@RunWith(SpringRunner.class)
@SpringBootTest
class MockMvcWithMockServiceTestNoDatabaseHit {


    //THIS       IS   FOR TESTING CONTROLLER THROUGH MOCKMVC

   

    @MockBean
    private ProductRepository repository;


    private MockMvc mockMvc;

    ObjectMapper om = new ObjectMapper();

    @Autowired
    private WebApplicationContext context;

    @Before
    private void setUp() {
        //   mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

    }

    //Testing is done for controller without hitting db. we are mocking repository

    @Test
    public void getProductByIdTestWithNoDb() throws Exception {

        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

        Vendor vendor = new Vendor(1L, "XYZ", "NEPAL KTM", "2092092090");
        Product p = new Product(4L, "Head Phone4", "Good and Nice1", LocalDate.of(2020, 9, 9), new BigDecimal(34.45), 23, vendor);


        Optional<Product> pOpt = Optional.of(p);


        when(repository.findById(4L)).thenReturn(pOpt);

        

        mockMvc.perform(get("/getbyid/4")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))


                .andExpect(jsonPath("$.expiryDate", Matchers.is(LocalDate.of(2020, 9, 9).toString())))
                .andExpect(jsonPath("$.unitPrice", Matchers.is(new BigDecimal(34.45))))
                .andExpect(jsonPath("$.name", Matchers.is("Head Phone4")))
                .andExpect(jsonPath("$.*", Matchers.hasSize(7)));
        
        
        verify(repository).findById(4L);
        
    }
    
  //  @Test  //Something is wrong with this
    public void postProductTestWithNoDb() throws Exception {

        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

        Vendor vendor1 = new Vendor(1L, "XYZ", "NEPAL KTM", "2092092090");
        Product p1 = new Product(5L, "Head Phone4", "Good and Nice1", LocalDate.of(2020, 9, 9), new BigDecimal(34.45), 23, vendor1);
        when(repository.save(p1)).thenReturn(p1);
        
        om.registerModule(new JavaTimeModule());
        om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        
      String jsonString=om.writeValueAsString(p1);
      
    //  System.out.println(jsonString);




        mockMvc.perform(post("/addproduct")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonString))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", Matchers.is("Head Phone4")))
                .andExpect(jsonPath("$.description", Matchers.is("Good and Nice1")))
                .andExpect(jsonPath("$.*", Matchers.hasSize(7)));

        
    //    verify(repository).save(p);
        
    }


}
