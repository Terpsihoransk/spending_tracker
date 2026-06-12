package spending.tracker.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import spending.tracker.backend.dto.MenuMeasureResponse;
import spending.tracker.backend.mapper.MenuMeasureMapper;
import spending.tracker.backend.service.data.MenuMeasureDataService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuMeasureService {

    private final MenuMeasureDataService menuMeasureDataService;
    private final MenuMeasureMapper menuMeasureMapper;

    public List<MenuMeasureResponse> getAllMeasures() {
        return menuMeasureDataService.findAll().stream()
                .map(menuMeasureMapper::toDto)
                .toList();
    }

    public MenuMeasureResponse getMeasureById(Long id) {
        var model = menuMeasureDataService.findById(id);
        return menuMeasureMapper.toDto(model);
    }
}