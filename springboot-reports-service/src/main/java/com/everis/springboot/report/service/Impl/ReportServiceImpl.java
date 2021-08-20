package com.everis.springboot.report.service.Impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.web.reactive.function.client.WebClient;

import com.everis.springboot.report.documents.MovementDocument;
import com.everis.springboot.report.documents.MovementReport;
import com.everis.springboot.report.service.ReportService;
import com.google.common.io.ByteStreams;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import reactor.core.publisher.Mono;

@Service
public class ReportServiceImpl implements ReportService {
	
	@Autowired
	private WebClient.Builder webClientBuilder;
	
	@Value("${everis.url.gateway}")
	private String urlGateway;

	@Override
	public Mono<ResponseEntity<?>> getComissionReport(String idCuenta, String fechaInicio, String fechaFin) {
		System.out.println("Entro a imprimir el pdf de comisiones");
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		List<MovementReport> movements = new ArrayList<>();
		
		return webClientBuilder.build().get()
		.uri(urlGateway+"/api/movement/getComissionsAccount/"+idCuenta+"/"+fechaInicio+"/"+fechaFin)
		.retrieve()
		.bodyToFlux(MovementDocument.class).doOnNext(movement ->{
			
			MovementReport mov = new MovementReport();
			mov.setTipoMovimiento(movement.getTipoMovimiento());
			mov.setTipoProducto(movement.getTipoProducto());
			mov.setFechaMovimiento(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(movement.getFechaMovimiento()));
			mov.setComission(movement.getComission());
			movements.add(mov);
			
		}).collectList().flatMap( movement -> {
						
			try {				
				File file = ResourceUtils.getFile("classpath:reportcomission.jrxml");
				
				JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
				
				JRBeanCollectionDataSource beanDataSource = new JRBeanCollectionDataSource(movements);
				
				JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, null, beanDataSource);
				
				JasperExportManager.exportReportToPdfStream(jasperPrint, out);
				
				byte[] targetArray = ByteStreams.toByteArray(new ByteArrayInputStream(out.toByteArray()));
				
				ByteArrayResource resource = new ByteArrayResource(targetArray);
				
				return Mono.just(ResponseEntity.ok()
						.header("Content-Disposition", "attachment;filename=Comission_Report.pdf")
						.contentType(MediaType.parseMediaType("application/octet-stream"))
						.body(resource));
				
			}catch(Exception e) {
				e.printStackTrace();
				throw new RuntimeException("Error al generar reporte en PDF");
			}
		});
		
		
		
	}

}
