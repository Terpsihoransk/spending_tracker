package spending.tracker.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import spending.tracker.backend.model.MessageDto;
import spending.tracker.backend.model.SpendingDto;
import spending.tracker.backend.service.SpendingService;

import java.util.List;

@Controller
@RequestMapping("api/v1/spending")
@RequiredArgsConstructor
public class SpendingController {

    private final SpendingService spendingService;

    @GetMapping
    public List<SpendingDto> getAllSpending(@RequestHeader("X-User-Email") String userId) {
        return spendingService.getAllSpending(userId);
    }

    @GetMapping("/{id}")
    public SpendingDto getSpendingById(@PathVariable Long id) {
        return spendingService.getSpendingById(id);
    }

    @PostMapping
    public SpendingDto createSpending(@RequestBody SpendingDto spendingDto, @RequestHeader("X-User-Email") String userId) {
        spendingDto.setUserId(userId);
        return spendingService.createSpending(spendingDto);
    }

    @PutMapping("/{id}")
    public SpendingDto updateSpending(@PathVariable Long id, @RequestBody SpendingDto spendingDto) {
        return spendingService.updateSpending(id, spendingDto);
    }

    @DeleteMapping("/{id}")
    public MessageDto deleteSpending(@PathVariable Long id) {
        boolean deleted = spendingService.deleteSpending(id);
        if (deleted) {
            return new MessageDto("запись успешно удалена");
        } else {
            return new MessageDto("запись не найдена");
        }
    }
}