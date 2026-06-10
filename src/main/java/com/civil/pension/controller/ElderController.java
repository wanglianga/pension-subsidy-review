package com.civil.pension.controller;

import com.civil.pension.common.PageResult;
import com.civil.pension.common.Result;
import com.civil.pension.entity.Elder;
import com.civil.pension.enums.ElderStatus;
import com.civil.pension.service.ElderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/elders")
public class ElderController {

    private final ElderService elderService;

    @Autowired
    public ElderController(ElderService elderService) {
        this.elderService = elderService;
    }


    @PostMapping
    public Result<Elder> create(@RequestBody Elder elder) {
        return Result.success(elderService.create(elder));
    }

    @PutMapping("/{id}")
    public Result<Elder> update(@PathVariable Long id, @RequestBody Elder elder) {
        return Result.success(elderService.update(id, elder));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        elderService.delete(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<Elder> getById(@PathVariable Long id) {
        return Result.success(elderService.getById(id));
    }

    @GetMapping("/id-card/{idCard}")
    public Result<Elder> getByIdCard(@PathVariable String idCard) {
        return Result.success(elderService.getByIdCard(idCard));
    }

    @GetMapping
    public Result<PageResult<Page<Elder>>> list(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String idCard,
            @RequestParam(required = false) String communityCode,
            @RequestParam(required = false) ElderStatus status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<Elder> page = elderService.list(name, idCard, communityCode, status, pageable);
        return Result.success(PageResult.of(page.getTotalElements(), pageNum, pageSize, page));
    }

    @PostMapping("/{id}/deceased")
    public Result<Elder> markDeceased(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate deathDate) {
        return Result.success(elderService.markDeceased(id, deathDate));
    }

    @PostMapping("/{id}/moved-out")
    public Result<Elder> markMovedOut(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate moveOutDate,
            @RequestParam(required = false) String moveToPlace) {
        return Result.success(elderService.markMovedOut(id, moveOutDate, moveToPlace));
    }
}
