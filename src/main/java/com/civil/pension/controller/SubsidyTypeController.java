package com.civil.pension.controller;

import com.civil.pension.common.PageResult;
import com.civil.pension.common.Result;
import com.civil.pension.entity.SubsidyType;
import com.civil.pension.service.SubsidyTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subsidy-types")
public class SubsidyTypeController {

    private final SubsidyTypeService subsidyTypeService;

    @Autowired
    public SubsidyTypeController(SubsidyTypeService subsidyTypeService) {
        this.subsidyTypeService = subsidyTypeService;
    }

    @PostMapping
    public Result<SubsidyType> create(@RequestBody SubsidyType subsidyType) {
        return Result.success(subsidyTypeService.create(subsidyType));
    }

    @PutMapping("/{id}")
    public Result<SubsidyType> update(@PathVariable Long id, @RequestBody SubsidyType subsidyType) {
        return Result.success(subsidyTypeService.update(id, subsidyType));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        subsidyTypeService.delete(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<SubsidyType> getById(@PathVariable Long id) {
        return Result.success(subsidyTypeService.getById(id));
    }

    @GetMapping("/code/{subsidyCode}")
    public Result<SubsidyType> getByCode(@PathVariable String subsidyCode) {
        return Result.success(subsidyTypeService.getByCode(subsidyCode));
    }

    @GetMapping
    public Result<PageResult<Page<SubsidyType>>> list(
            @RequestParam(required = false) String subsidyCode,
            @RequestParam(required = false) String subsidyName,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<SubsidyType> page = subsidyTypeService.list(subsidyCode, subsidyName, isActive, pageable);
        return Result.success(PageResult.of(page.getTotalElements(), pageNum, pageSize, page));
    }

    @GetMapping("/active")
    public Result<List<SubsidyType>> listActive() {
        return Result.success(subsidyTypeService.listActive());
    }
}
