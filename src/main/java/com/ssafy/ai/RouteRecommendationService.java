package com.ssafy.ai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RouteRecommendationService {

    @Autowired
    private ChatModel chatModel;

    /**
     * 경로 추천 JSON을 받아서 AI로 더 효율적인 경로 추천 결과를 반환
     */
    public String recommendRoute(String routeJson) {
        // 프롬프트 메시지 생성
        String promptMessage = buildPromptMessage(routeJson);

        System.out.println(promptMessage);
        
        // AI 호출
        ChatClient client = ChatClient.create(chatModel);
        String result = client.prompt()
            .user(promptMessage)
            .call()
            .content();

        return result;
    }

    /**
     * AI 프롬프트 메시지 생성
     */
    private String buildPromptMessage(String routeJson) {
    	return """
    			Please refer to the input JSON structure below and recommend the optimal route using the latitude and longitude coordinates of each destination and the visit order (day, order).
    			Input example: %s

    			The route calculation should follow the Multi-day Traveling Salesman Problem (mTSP) algorithm.
    			For each day, optimize the schedule to minimize travel distance and time based on the coordinates of the destinations.
    			It is allowed to move destinations to different days, and the number of destinations visited per day can also be changed to achieve the overall optimal travel time.
    			The visit order (day, order) of each destination may differ from the input and can be rearranged according to the mTSP optimization.

    			The result must be returned strictly in the following JSON format only, without any additional explanations or text:

    			{
    			  "dayRoutes": [
    			    {
    			      "day": 1,
    			      "visits": [
    			        { "day": 1, "order": 1 },
    			        { "day": 1, "order": 2 }
    			      ],
    			      "start": "custom-1",
    			      "end": "custom-1",
    			      "duration": 4210
    			    },
    			    {
    			      "day": 2,
    			      "visits": [
    			        { "day": 2, "order": 1 }
    			      ],
    			      "start": "custom-1",
    			      "end": "custom-1",
    			      "duration": 2120
    			    },
    			    {
    			      "day": 3,
    			      "visits": [],
    			      "start": "custom-1",
    			      "end": "custom-1",
    			      "duration": 0
    			    }
    			  ]
    			}
    			""".formatted(routeJson);
    }


}
