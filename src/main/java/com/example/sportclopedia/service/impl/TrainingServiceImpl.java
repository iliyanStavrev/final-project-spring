package com.example.sportclopedia.service.impl;

import com.example.sportclopedia.error.TrainingTimeViolation;
import com.example.sportclopedia.model.dto.AddTrainingDto;
import com.example.sportclopedia.model.dto.TrainingDto;
import com.example.sportclopedia.model.entity.Training;
import com.example.sportclopedia.model.entity.User;
import com.example.sportclopedia.model.enums.IntensityLevelEnum;
import com.example.sportclopedia.repository.TrainingRepository;
import com.example.sportclopedia.repository.UserRepository;
import com.example.sportclopedia.service.*;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TrainingServiceImpl implements TrainingService {

    private final TrainingRepository trainingRepository;
    private final SportService sportService;
    private final CoachService coachService;
    private final HallService hallService;
    private final UserService userService;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public TrainingServiceImpl(TrainingRepository trainingRepository, SportService sportService,
                               CoachService coachService, HallService hallService,
                               UserService userService, UserRepository userRepository, ModelMapper modelMapper) {
        this.trainingRepository = trainingRepository;
        this.sportService = sportService;
        this.coachService = coachService;
        this.hallService = hallService;
        this.userService = userService;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public void initTraining() {
        if (trainingRepository.count() > 0) {
            return;
        }
        Training training = new Training();
        training.setDuration(55);
        training.setIntensity(IntensityLevelEnum.Intermediate);
        training.setName("Training for speed and agility");
        training.setStartedOn(LocalDateTime
                .parse("2022-08-21 at 19:45",
                        DateTimeFormatter.ofPattern("yyyy-MM-dd 'at' HH:mm")));
        training.setSport(sportService.findByName("Basketball"));
        training.setCoach(coachService.findById(1L));
        training.setHall(hallService.findByName("SILA"));
        training.setDescription("Basketball is an extremely dynamic sport that requires movements in multiple planes of motion as well as rapid transitions from jogging to sprinting to jumping. The ability to quickly elude defenders, rapidly decelerate to take a jump shot, or explosively jump up to grab a rebound are all skills required to effectively play the sport.It is equally important for the athlete to be able to perform these skills in a variety of directions and in a controlled manner to ensure injuries do not ensue. Due to the myriad of physical demands that come with the sport makes speed and agility training a crucial component to incorporate into a basketball training program.");
        trainingRepository.save(training);

        Training training1 = new Training();
        training1.setDuration(75);
        training1.setIntensity(IntensityLevelEnum.High);
        training1.setName("Power moves and explosion");
        training1.setStartedOn(LocalDateTime
                .parse("2022-08-21 at 19:45",
                        DateTimeFormatter.ofPattern("yyyy-MM-dd 'at' HH:mm")));
        training1.setSport(sportService.findByName("Basketball"));
        training1.setCoach(coachService.findById(1L));
        training1.setHall(hallService.findByName("SILA"));
        training1.setDescription("The power move is executed by pivoting towards the basket to seal the defender, then using a two handed power dribble followed by a jump stop to get closer to the basket. Immediately after the jump stop the player jumps up for a power shot or jump hook.");
        trainingRepository.save(training1);
    }

    @Override
    public List<TrainingDto> getTrainingsBySport(String sportName) {

        List<Training> trainings = trainingRepository
                .findBySport_Name(sportName);
        return trainings
                .stream()
                .map(training -> {
                    TrainingDto trainingDto = modelMapper
                            .map(training, TrainingDto.class);
                    trainingDto.setCoachFullName(training.getCoach().getFullName());
                    trainingDto.setSportName(training.getSport().getName());
                    trainingDto.setHallName(training.getHall() == null
                            ? "There is no Hall yet!"
                            : training.getHall().getName());
                    return trainingDto;
                })
                .collect(Collectors.toList());

    }

    @Override
    public void addTraining(AddTrainingDto addTrainingDto) {

        Training training = modelMapper
                .map(addTrainingDto, Training.class);

        training.setIntensity(IntensityLevelEnum.valueOf(addTrainingDto.getIntensity()));
        training.setSport(sportService.findByName(addTrainingDto.getSport()));
        training.setHall(hallService.findByName(addTrainingDto.getHall()));
        training.setCoach(coachService.findByName(addTrainingDto.getCoach()));

        trainingRepository.save(training);
    }

    @Override
    public void deleteTraining(Long id) {

        Training training = trainingRepository
                .findById(id).orElse(null);
        if (LocalDateTime.now().isAfter(training.getStartedOn())) {

             userRepository.findAll()
                    .stream()
                    .filter(user -> user.getTrainings().contains(training))
                    .forEach(user -> user.getTrainings().remove(training));

            trainingRepository
                    .deleteById(id);
        } else {
            throw new TrainingTimeViolation(training.getName(), training.getStartedOn());
        }
    }

    @Override
    public List<TrainingDto> getAllTrainings() {

        return trainingRepository
                .findAll()
                .stream()
                .map(t -> modelMapper
                        .map(t, TrainingDto.class))
                .toList();
    }

    @Override
    public boolean reserveTraining(Long id) {

        User user = getUser();
        Training training = trainingRepository
                .findById(id).orElse(null);
        List<Training> trainings = user.getTrainings();

        if (!trainings.contains(training)) {
            trainings.add(training);
            user.setTrainings(trainings);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    private User getUser() {
        Object principal = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        String username = ((UserDetails) principal).getUsername();
        return userService.findByUsername(username);
    }

    @Override
    public List<TrainingDto> getAllUserTrainings() {

        User user = getUser();

        return user.getTrainings()
                .stream()
                .map(t -> modelMapper
                        .map(t, TrainingDto.class))
                .toList();
    }

    @Override
    public void removeTrainingFromUser(Long id) {

        User user = getUser();
        user.getTrainings().remove(trainingRepository.findById(id).orElse(null));
        userRepository.save(user);
    }

    @Override
    public boolean isTrainingAdded(AddTrainingDto addTrainingDto) {
        Training training = trainingRepository
                .findByNameAndSport_NameAndStartedOn(addTrainingDto.getName(),
                        addTrainingDto.getSport(), addTrainingDto.getStartedOn());
        return training != null;
    }

    @Override
    public void deleteExpiredTraining() {

        List<Training> trainings = trainingRepository
                .findAll()
                .stream()
                .filter(training -> training.getStartedOn().isBefore(LocalDateTime.now()))
                .filter(training -> training.getUsers().isEmpty())
                .toList();

        trainingRepository.deleteAll(trainings);
    }

    @Override
    public TrainingDto findById(Long id) {

        return trainingRepository
                .findById(id)
                .map(training -> modelMapper
                        .map(training, TrainingDto.class))
                .orElse(null);
    }

    @Override
    public List<TrainingDto> getBestTrainings() {

        return trainingRepository
                .findAll()
                .stream()
                .sorted((a,b) -> b.getUsers().size() - a.getUsers().size())
                .limit(3)
                .map(training -> modelMapper
                        .map(training, TrainingDto.class))
                .toList();

    }
}
