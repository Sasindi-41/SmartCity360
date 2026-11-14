package lk.sasindi.smartcity360.patterns.composite;

public class Street extends AbstractCompositeComponent {

    public Street(
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
                "Building"
        );
    }

    @Override
    public String getType() {
        return "Street";
    }
}
