package com.everis.springboot.movements.service.Impl;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Service;

import com.everis.springboot.movements.dao.MovementDao;
import com.everis.springboot.movements.documents.MovementDocument;
import com.everis.springboot.movements.service.MovementService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class MovementServiceImpl implements MovementService {
	
	@Autowired
	private MovementDao movementDao;

	@Override
	public Mono<MovementDocument> saveMovement(MovementDocument movement) {
		return movementDao.save(movement);
	}

	@Override
	public Mono<Long> getNumberOfMovements(String idCuenta) {

		LocalDate todaydate = LocalDate.now();
		SimpleDateFormat sdformat = new SimpleDateFormat("dd-MM-yyyy");
		
		return movementDao.findByIdCuenta(idCuenta).filter( a->{
			
		      Date d1;
		      Date d2;
		      boolean isInThisMonth=false;
			try {
				
				d1 = sdformat.parse(sdformat.format(a.getFechaMovimiento()));
			
		      
		      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		      String formattedString = todaydate.withDayOfMonth(1).format(formatter);
		       d2 = sdformat.parse(formattedString);
		     System.out.print(d2 + " "+d1);
		     isInThisMonth= d1.compareTo(d2) > 0 || d1.compareTo(d2) ==0;
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return isInThisMonth;
		}  ).count();
	}

	@Override
	public Flux<MovementDocument> getComissionsByAccount(String idCuenta, String fechaInicio, String fechaFin) throws ParseException {
		Date dateInicio = new SimpleDateFormat("dd-MM-yyyy").parse(fechaInicio);
		Date dateFin = new SimpleDateFormat("dd-MM-yyyy").parse(fechaFin);
		return movementDao.findMovementDocumentByFechaMovimientoAndIdCliente(dateInicio, dateFin, idCuenta);
	}

}
