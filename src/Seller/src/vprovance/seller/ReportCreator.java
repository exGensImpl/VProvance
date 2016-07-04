/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vprovance.seller;

import VProvance.Core.Database.DBConnection;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.style.Styler;

/**
 *
 * @author DexpUser
 */
public class ReportCreator {
    DBConnection _connection;
            
    public ReportCreator(DBConnection connection) {
        _connection = connection;
    }
    
    public WineSaleReport GetWineSaleReport() {
        WineSaleReport res = new WineSaleReport();
        
        res.addAll(Arrays.asList(
                _connection.GetWineSales()
                        .stream()
                        .map(s -> new WineSaleEntry(s.getWineType(), s.getMonth(), s.getCost()))
                        .toArray(s -> new WineSaleEntry[s])));
        
        return res;
    }
}

class WineSaleEntry {
    private final String _description;
    private final Date _date;
    private final BigDecimal _cost;
    
    public WineSaleEntry (String description, Date date, BigDecimal cost) {
        _description = description;
        _date = date;
        _cost = cost;
    }
    
    public String getDescription() {
        return _description;
    }
    
    public Date getDate() {
        return _date;
    }
    
    public BigDecimal getCost() {
        return _cost;
    }
}

class WineSaleReport extends ArrayList<WineSaleEntry> {
    public void ShowChart() {
        CategoryChart chart = getChart();
        new SwingWrapper<>(chart).displayChart();
    }
    
    public CategoryChart getChart() {
        // Create Chart
        CategoryChart chart = new CategoryChartBuilder().width(600).height(500)
                    .title("Динамика продаж вина").xAxisTitle("Месяц").yAxisTitle("Сумма").build();

        chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNW);
        chart.getStyler().setHasAnnotations(true);
            
        DateFormat df = new SimpleDateFormat("MM/yyyy");
        List<String> months = Arrays.asList(this.stream().map(entry -> df.format(entry.getDate())).distinct().toArray(s -> new String[s])); 

        // Series
        this.stream().map(entry -> entry.getDescription()).distinct().forEach((wineName) -> {
            chart.addSeries(wineName,
                    months,
                    getMonthsCreditByWine(wineName, months, df));
        });
        return chart;
    }
    
    private List<BigDecimal> getMonthsCreditByWine(String wineType, List<String> months, DateFormat df) {
        
        return Arrays.asList(months.stream().map(m -> this
                    .stream()
                    .filter(e -> (df.format(e.getDate()).equals(m) &&
                                e.getDescription().equals(wineType)))
                    .map(e -> e.getCost()).reduce(BigDecimal.ZERO, (d,a) -> a))
                    .toArray(s -> new BigDecimal[s]));        
    }
}
