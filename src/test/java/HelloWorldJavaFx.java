

import java.io.IOException;

import org.praisenter.data.slide.effects.ShadowType;
import org.praisenter.data.slide.effects.SlideShadow;
import org.praisenter.data.slide.graphics.SlideColor;
import org.praisenter.data.slide.graphics.SlideGradient;
import org.praisenter.data.slide.graphics.SlideGradientCycleType;
import org.praisenter.data.slide.graphics.SlideGradientStop;
import org.praisenter.data.slide.graphics.SlideGradientType;
import org.praisenter.ui.slide.controls.SlideGradientPicker;
import org.praisenter.ui.slide.controls.SlideShadowPicker;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class HelloWorldJavaFx extends Application {
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) throws IOException {
//    	int spacing = 5;
//    	
//    	Directory d = FSDirectory.open(Paths.get(Constants.SEARCH_INDEX_ABSOLUTE_PATH));
//    	Analyzer a = new StandardAnalyzer();
//    	SearchIndex si = new SearchIndex(d, a);
//    	DataManager dm = new DataManager(si);
//    	Configuration c = new Configuration();
//    	GlobalContext gc = new GlobalContext(this, primaryStage, dm, c);
//    	
//    	dm.registerPersistAdapter(Slide.class, new SlidePersistAdapter(Paths.get(Constants.SLIDES_ABSOLUTE_PATH), new JavaFXSlideRenderer(gc), c));
//    	
//    	VBox root = new VBox();
//    	root.setBackground(new Background(new BackgroundFill(Color.RED, null, null)));
//    	root.setAlignment(Pos.TOP_LEFT);
//    	
//    	SlideView sv = new SlideView(gc);
//    	//sv.setPrefSize(512, 384);
//    	sv.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
//    	sv.setMinSize(0, 0);
//    	sv.setViewMode(SlideMode.VIEW);
//    	sv.setViewScalingEnabled(true);
//    	
//    	Slide s = new Slide();
//    	s.setBackground(new SlideColor(0.5,0.5,0.5,0.5));
//    	//s.setBorder(new SlideStroke(new SlideColor(0, 1, 0, 1), new SlideStrokeStyle(SlideStrokeType.CENTERED, SlideStrokeJoin.MITER, SlideStrokeCap.SQUARE), 1, 10));
//    	s.setCreatedDate(Instant.now());
//    	s.setHeight(768);
//    	s.setModifiedDate(Instant.now());
//    	s.setName("test");
//    	s.setOpacity(1);
//    	s.setWidth(1024);
//    	
//    	TextComponent tc = new TextComponent();
//    	tc.setBackground(new SlideColor(0, 1, 0, 1));
//    	tc.setText("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Ut mauris purus, eleifend ac urna nec, venenatis placerat lectus. Vestibulum at sagittis nulla, sed euismod diam. Vivamus vel odio rhoncus turpis ultrices tristique sed feugiat nunc. Nulla condimentum magna in neque sagittis, vel pharetra diam pellentesque. Aliquam erat volutpat. Cras consequat lectus eu odio rhoncus, ut faucibus nisl congue. Curabitur sed elit a neque aliquam rutrum. Vivamus venenatis consectetur malesuada.");
//    	tc.setFontScaleType(FontScaleType.REDUCE_SIZE_ONLY);
//    	tc.setFont(new SlideFont("Segoe UI Light", SlideFontWeight.BOLD, SlideFontPosture.REGULAR, 20));
//    	tc.setPadding(new SlidePadding(10));
//    	tc.setTextBorder(new SlideStroke(new SlideColor(1, 0, 0, 1), new SlideStrokeStyle(SlideStrokeType.CENTERED, SlideStrokeJoin.MITER, SlideStrokeCap.SQUARE), 1, 10));
//    	tc.setTextPaint(new SlideColor(0, 0, 1, 1));
//    	tc.setWidth(300);
//    	tc.setHeight(100);
//    	tc.setX(200);
//    	tc.setY(200);
//    	
//    	TextComponent tc2 = new TextComponent();
//    	tc2.setBackground(new SlideColor(0, 0, 1, 1));
//    	tc2.setBorder(new SlideStroke(new SlideColor(0, 1, 0, 1), new SlideStrokeStyle(SlideStrokeType.CENTERED, SlideStrokeJoin.MITER, SlideStrokeCap.SQUARE), 1, 10));
//    	tc2.setText("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Ut mauris purus, eleifend ac urna nec, venenatis placerat lectus. Vestibulum at sagittis nulla, sed euismod diam. Vivamus vel odio rhoncus turpis ultrices tristique sed feugiat nunc. Nulla condimentum magna in neque sagittis, vel pharetra diam pellentesque. Aliquam erat volutpat. Cras consequat lectus eu odio rhoncus, ut faucibus nisl congue. Curabitur sed elit a neque aliquam rutrum. Vivamus venenatis consectetur malesuada.");
//    	tc2.setFontScaleType(FontScaleType.NONE);
//    	tc2.setFont(new SlideFont("Segoe UI Light", SlideFontWeight.BOLD, SlideFontPosture.REGULAR, 20));
//    	tc2.setPadding(new SlidePadding(10));
////    	tc2.setTextBorder(new SlideStroke(new SlideColor(1, 0, 0, 1), new SlideStrokeStyle(SlideStrokeType.CENTERED, SlideStrokeJoin.MITER, SlideStrokeCap.SQUARE), 1, 10));
//    	tc2.setTextPaint(new SlideColor(0, 0, 0, 1));
//    	tc2.setWidth(200);
//    	tc2.setHeight(100);
//    	tc2.setX(900);
//    	tc2.setY(500);
//    	
//    	s.getComponents().add(tc);
//    	s.getComponents().add(tc2);
//    	
//    	sv.setSlide(s);
//    	
//    	SlideEditor se = new SlideEditor(gc, new DocumentContext<Slide>(s));
//    	se.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
//    	se.setMinSize(0, 0);
//    	
//    	Button btnSave = new Button("save new");
//    	btnSave.setOnAction(e -> {
//    		gc.getDataManager().create(s);
//    	});
//    	
//    	root.getChildren().addAll(se, sv, btnSave);
////    	VBox.setVgrow(sv, Priority.ALWAYS);
//    	VBox.setVgrow(se, Priority.ALWAYS);
        
    	SlideGradientPicker sgp = new SlideGradientPicker();
    	SlideShadowPicker ssp = new SlideShadowPicker("");
    	
    	VBox layout = new VBox(
    			sgp,
    			ssp);

    	
        primaryStage.setScene(new Scene(layout, 500, 300));
        primaryStage.show();
        
    	SlideGradient slg = new SlideGradient();
    	slg.setType(SlideGradientType.RADIAL);
    	slg.setCycleType(SlideGradientCycleType.REFLECT);
    	slg.setEndX(0.8);
    	slg.setEndY(0.9);
    	slg.setStartX(0.2);
    	slg.setStartY(0.1);
    	slg.getStops().clear();
    	slg.getStops().add(new SlideGradientStop(0.1, new SlideColor(0.0, 0.8, 0.5, 0.8)));
    	slg.getStops().add(new SlideGradientStop(0.7, new SlideColor(0.5, 0.5, 0.0, 1.0)));
    	sgp.setValue(slg);
    	
    	SlideShadow ss = new SlideShadow();
    	ss.setColor(new SlideColor(1.0, 0.0, 0.0, 1.0));
    	ss.setOffsetX(1.0);
    	ss.setOffsetY(2.0);
    	ss.setRadius(1.0);
    	ss.setSpread(0.0);
    	ss.setType(ShadowType.OUTER);
    	ssp.setValue(ss);
    }
}