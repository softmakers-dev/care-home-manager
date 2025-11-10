package com.softmakers.manager_service.resource;

import com.softmakers.manager_domain.entity.dto.dm.*;
import com.softmakers.manager_domain.spec.ChatService;
import com.softmakers.manager_domain.spec.UserService;
import com.softmakers.result.ResultResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.softmakers.result.ResultCode.*;

@Validated
@RestController
@RequiredArgsConstructor
public class ChatController {

    private final UserService userService;
    private final ChatService chatService;

    @Operation( description = "채팅방 생성")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "C001 - 채팅방 생성에 성공하였습니다."),
            @ApiResponse(responseCode = "400", description = "G003 - 유효하지 않은 입력입니다.\n"
                    + "G004 - 입력 타입이 유효하지 않습니다."),
            @ApiResponse(responseCode = "401", description = "F003 - 로그인이 필요한 화면입니다.")
    })
    @PostMapping("/chat/rooms")
    public ResponseEntity<ResultResponse> createChatRoom(
            @RequestParam("usernames") List<@NotEmpty @Size(max = 12) String> usernames) {
        final ChatRoomCreateResponse response = chatService.createRoom( usernames,
                this.userService.findLoginUser() );

        return ResponseEntity.ok(ResultResponse.of(CREATE_CHAT_ROOM_SUCCESS, response));
    }

    @Operation(
            summary = "채팅방 목록 페이징 조회",
            description = "페이지당 10개씩 조회할 수 있습니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "C004 - 채팅방 목록 조회에 성공하였습니다."),
            @ApiResponse(responseCode = "400", description = "G003 - 유효하지 않은 입력입니다.\nG004 - 입력 타입이 유효하지 않습니다."),
            @ApiResponse(responseCode = "401", description = "M003 - 로그인이 필요한 화면입니다.")
    })
    @Parameter(name = "page", description = "페이지", example = "1", required = true)
    @GetMapping("/chat/rooms")
    public ResponseEntity<ResultResponse> getJoinRooms(@RequestParam("page") Integer page) {
        final Page<JoinRoomDto> response = chatService.getJoinRooms(page);

        return ResponseEntity.ok(ResultResponse.of(GET_JOIN_ROOMS_SUCCESS, response));
    }

    @Operation(
            summary = "채팅방 메시지 목록 페이징 조회",
            description = "페이지당 10개씩 조회할 수 있습니다.")
    @ApiResponses ({
            @ApiResponse(
                    responseCode = "200",
                    description = "C005 - 채팅 메시지 목록 조회에 성공하였습니다.",
                    content = @Content(schema = @Schema(implementation = List.class)) // Replace List.class with your actual successful return type if needed
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "G003 - 유효하지 않은 입력입니다.\n"
                            + "G004 - 입력 타입이 유효하지 않습니다."
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "M003 - 로그인이 필요한 화면입니다."
            )
    })
    @Parameter(name = "roomId", description = "채팅방 PK", example = "1", required = true)
    @Parameter(name = "page", description = "페이지", example = "1", required = true)
    @GetMapping("/chat/rooms/{roomId}/messages")
    public ResponseEntity<ResultResponse> getChatMessages(@PathVariable("roomId") Long roomId,
                                                          @RequestParam("page") Integer page) {
        final Page<MessageDto> response = chatService.getChatMessages(roomId, page);

        return ResponseEntity.ok(ResultResponse.of(GET_CHAT_MESSAGES_SUCCESS, response));
    }

    @Operation(summary = "채팅방 조회")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "C002 - 채팅방 조회에 성공하였습니다."
                    // You would typically add @Content here to define the response body schema
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "클라이언트 에러",
                    content = @Content(
                            // Assuming your error response is a standard JSON object (e.g., ErrorResponse.class)
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(name = "G003", value = "G003 - 유효하지 않은 입력입니다."),
                                    @ExampleObject(name = "G004", value = "G004 - 입력 타입이 유효하지 않습니다."),
                                    @ExampleObject(name = "C001", value = "C001 - 존재하지 않는 채팅방입니다.")
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "M003 - 로그인이 필요한 화면입니다."
            )
    })
    @DeleteMapping("/chat/rooms/{roomId}")
    public ResponseEntity<ResultResponse> inquireChatRoom(@PathVariable("roomId") Long roomId) {
        final ChatRoomInquireResponse response = chatService.inquireRoom(roomId);

        return ResponseEntity.ok(ResultResponse.of(INQUIRE_CHAT_ROOM_SUCCESS, response));
    }

    @MessageMapping("/messages")
    public void sendTextMessage(@Valid @RequestBody MessageRequest request) {
        chatService.sendMessage(request);
    }
}
