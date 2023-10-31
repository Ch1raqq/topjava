package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

        List<UserMealWithExcess> mealsTo = filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);

        List<UserMealWithExcess> mealsTo2 = getFilteredWithExceedInOnePass(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo2.forEach(System.out::println);
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        System.out.println("--------------------------------------");
        int caloriesSumByDate = meals.stream()
                .mapToInt(UserMeal::getCalories)
                .sum();
        return meals
                .stream()
                .filter(m -> TimeUtil.isBetweenInclusive(m.getDateTime().toLocalTime(), startTime, endTime))
                .sorted()
                .map(um -> new UserMealWithExcess(um.getDateTime(), um.getDescription(),
                        um.getCalories(), caloriesSumByDate > caloriesPerDay))
                .collect(toList());
    }
    public static List<UserMealWithExcess> getFilteredWithExceedInOnePass(List<UserMeal> meals, LocalTime startTime,
                                                                          LocalTime endTime, int caloriesPerDay) {
        Map<LocalDateTime, List<UserMeal>> groupedMeals = meals.stream()
                .collect(Collectors.groupingBy(UserMeal::getDateTime));

        List<UserMealWithExcess> filteredMeals = new ArrayList<>();
        for (List<UserMeal> dayMeals : groupedMeals.values()) {
            int caloriesSumByDate = dayMeals.stream()
                    .mapToInt(UserMeal::getCalories)
                    .sum();

            List<UserMealWithExcess> dayFilteredMeals = dayMeals.stream()
                    .filter(m -> TimeUtil.isBetweenInclusive(m.getDateTime().toLocalTime(), startTime, endTime))
                    .map(um -> new UserMealWithExcess(um.getDateTime(), um.getDescription(), um.getCalories(),
                            caloriesPerDay > caloriesSumByDate))
                    .collect(Collectors.toList());
            filteredMeals.addAll(dayFilteredMeals);
        }
        return filteredMeals;
    }
}
