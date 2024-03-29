package com.example.sportclopedia.web;

import com.example.sportclopedia.model.dto.SportDto;
import com.example.sportclopedia.model.dto.TrainingDto;
import com.example.sportclopedia.service.SportService;
import com.example.sportclopedia.service.TrainingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


import java.util.List;

@org.springframework.web.bind.annotation.RestController
public class RestController {

    private final SportService sportService;
    private final TrainingService trainingService;

    public RestController(SportService sportService, TrainingService trainingService) {
        this.sportService = sportService;
        this.trainingService = trainingService;
    }
    @GetMapping("/sports/api/all")
    public ResponseEntity<List<SportDto>>getAllSports(){
        return ResponseEntity
                .ok(sportService.findAll());
    }

//    @GetMapping("/all")
//    public ResponseEntity<SportDto[]> homePage(){
//   //     List<SportDto> sports = sportService.findAll();
////        if (sportDto == null){
////            return ResponseEntity
////                    .notFound()
////                    .build();
////        }else {
////           return ResponseEntity.ok(sportDto);
////        }
//
//       return restTemplate
//                .getForEntity("http://localhost:8080/sports", SportDto[].class);
//
//    }

    @GetMapping("sports/api/details/{id}")
    public ResponseEntity<List<TrainingDto>> sportDetails(@PathVariable Long id){

        SportDto sportDto = sportService.findById(id);
       List<TrainingDto> trainingDto = trainingService
                .getTrainingsBySport(sportDto.getName());
        return ResponseEntity.ok(trainingDto);
    }

}
