package lk.sasindi.smartcity360.patterns.composite;

public class City extends AbstractCompositeComponent {

    public City(
            int id,
            String name,
            String code,
            String description
    ) {
        super(
                id,
                name,
                code,
                description,
                "District"
        );
    }

    @Override
    public String getType() {
        return "City";
    }
}
