package com.SpringLibrary.SpringbootLibrary;

/**
 * Created by ricky.clevinger on 7/12/2017.
 */
import Model.Book;
import Model.Member;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import com.vaadin.ui.renderers.TextRenderer;
import com.vaadin.ui.themes.ValoTheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

@SpringView(name = CheckOut.VIEW_NAME)
public class CheckOut extends VerticalLayout implements View {
    public static final String VIEW_NAME = "CheckOut";

    HorizontalLayout hLayout;
    Button checkOut;
    private TextField titleFilter;   // TextField will be used to filter the results on the grid.
    private TextField authorFilter;   // TextField will be used to filter the results on the grid.
    String titleId;  // Id used to determine which item is selected in the grid.
    String memberId;  // Id used to determine which item is selected in the grid.
    RestTemplate restTemplate = new RestTemplate();  // RestTemplate used to make calls to micro-service.
    List<Book> books; // Used to store data retrieved from micro-service. Placed into the grid.
    List<Member> members; // Used to store data retrieved from micro-service. Placed into the grid.

    @Value("${my.bookUrl}")
    private String bookUrl;

    @Value("${my.memberUrl}")
    private String memUrl;

    @PostConstruct
    void init() {

        createLayout();
        createBookGrid();
        createMemberGrid();
        addCheckOutButton();


    }

    private void addCheckOutButton() {

        checkOut = new Button ("Check Out");

        //checkOut.addClickListener(event -> {
        //    this.restTemplate.getForObject(memUrl + "/members/delete/" + this.id, String.class);
       //     getUI().getNavigator().navigateTo(AllMembers.VIEW_NAME);
      //  });
        
        addComponent(checkOut);

    }
    private void createMemberGrid() {

        members = Arrays.asList(restTemplate.getForObject(memUrl + "/members/all", Member[].class));
        Grid<Member> memberGrid = new Grid<>();
        memberGrid.setWidth(100, Unit.PERCENTAGE);

        // Retrieves the data from the book micro-service.


        // Create a grid and adds listener to record selected item.
        memberGrid = new Grid<>();
        memberGrid.addSelectionListener(event -> {
            this.memberId = event.getFirstSelectedItem().get().getId() + "";
        });

        // Sets the width of the grid.
        memberGrid.setWidth(100, Unit.PERCENTAGE);
        // Sets list to the grid
        memberGrid.setItems(members);
        //Specifies what parts of the objects in the grid are shown.
        memberGrid.addColumn(Member::getFName, new TextRenderer()).setCaption("First Name");
        memberGrid.addColumn(Member::getLName, new TextRenderer()).setCaption("Last Name");

        hLayout.addComponent(memberGrid);
    }

    private void createBookGrid() {

        books = Arrays.asList(restTemplate.getForObject(bookUrl + "/books/all", Book[].class));
        Grid<Book> bookGrid = new Grid<>();

        bookGrid.addSelectionListener(event -> {
            this.titleId = event.getFirstSelectedItem().get().getBookId() + "";
        });

        // Sets the width of the grid.
        bookGrid.setWidth(100, Unit.PERCENTAGE);
        // Sets list to the grid
        bookGrid.setItems(books);
        //Specifies what parts of the objects in the grid are shown.
        bookGrid.addColumn(Book::getTitle, new TextRenderer()).setCaption("Title");
        bookGrid.addColumn(Book ->
                Book.getAuthFName() + " " + Book.getAuthLName()).setCaption("Author");

        bookGrid.setWidth(100, Unit.PERCENTAGE);

        hLayout.addComponent(bookGrid);

    }

    private void createLayout() {

        hLayout = new HorizontalLayout();
        hLayout.setSpacing(true);
        addComponent(hLayout);

    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        // This view is constructed in the init() method()
    }
}
