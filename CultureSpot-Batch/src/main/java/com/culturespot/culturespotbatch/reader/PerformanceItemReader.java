package com.culturespot.culturespotbatch.reader;

import com.culturespot.culturespotapi.api.BatchApiService;
import com.culturespot.culturespotdomain.performance.dto.PerformanceDto;
import com.culturespot.culturespotdomain.performance.dto.PerformanceResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@Slf4j
public class PerformanceItemReader implements ItemReader<PerformanceDto>, ItemStream {

	private static final int chunkSize = 1000;
	private static final String CURRENT_PAGE_KEY = "current.page.exhibition";
	private static final String END_OF_DATA_KEY = "end.of.data.exhibition";

	private final BatchApiService batchApiService;

	private StepExecution stepExecution;
	private Queue<PerformanceDto> itemQueue;
	private int currentPage = 1;
	private boolean isEndOfData = false;

	public PerformanceItemReader(BatchApiService batchApiService) {
		this.batchApiService = batchApiService;
		this.itemQueue = new LinkedList<>();
	}

	@Override
	public PerformanceDto read() throws Exception {
		if (itemQueue.isEmpty() && !isEndOfData) {
			fetchDataFromApi();
		}

		return itemQueue.poll();
	}

	private void fetchDataFromApi() throws Exception {
		PerformanceResponse response = batchApiService.performanceOpenApiCall(currentPage, chunkSize);
		processApiResponse(response);
	}

	private void processApiResponse(PerformanceResponse response) {
		List<PerformanceResponse.Item> responseItems = response.getBody().getItems().getItemList();
		List<PerformanceDto> dtoItems = convertToDtoList(responseItems);

		if (!dtoItems.isEmpty()) {
			itemQueue.addAll(dtoItems);
		}

		int currentPageNo = response.getBody().getPageNo();
		int numOfRows = response.getBody().getNumOfRows();
		int totalCount = response.getBody().getTotalCount();

		boolean isLastPage = (currentPageNo * numOfRows >= totalCount);
		if (isLastPage) {
			isEndOfData = true;
		}

		currentPage++;
		updateExecutionContext();
	}

	private List<PerformanceDto> convertToDtoList(List<PerformanceResponse.Item> items) {
		List<PerformanceDto> dtoList = new ArrayList<>();

		for (PerformanceResponse.Item item : items) {
			PerformanceDto dto = new PerformanceDto();
			dto.setSeq(item.getSeq());
			dto.setTitle(item.getTitle());
			dto.setServiceName(item.getServiceName());
			dto.setStartDate(item.getStartDate());
			dto.setEndDate(item.getEndDate());
			dto.setPlace(item.getPlace());
			dto.setRealmName(item.getRealmName());
			dto.setArea(item.getArea());
			dto.setThumbnail(item.getThumbnail());
			dto.setGpsX(item.getGpsX());
			dto.setGpsY(item.getGpsY());

			dtoList.add(dto);
		}

		return dtoList;
	}

	private void updateExecutionContext() {
		if (stepExecution != null) {
			ExecutionContext executionContext = stepExecution.getExecutionContext();
			executionContext.putInt(CURRENT_PAGE_KEY, currentPage);
		}
	}

	@Override
	public void open(ExecutionContext executionContext) throws ItemStreamException {
		if (executionContext.containsKey(CURRENT_PAGE_KEY)) {
			currentPage = executionContext.getInt(CURRENT_PAGE_KEY);
		} else {
			currentPage = 1;
		}

		itemQueue = new LinkedList<>();
	}

	@Override
	public void update(ExecutionContext executionContext) throws ItemStreamException {
		executionContext.putInt(CURRENT_PAGE_KEY, currentPage);
		executionContext.put(END_OF_DATA_KEY, isEndOfData);
	}

	@Override
	public void close() throws ItemStreamException {
		itemQueue.clear();
	}
}