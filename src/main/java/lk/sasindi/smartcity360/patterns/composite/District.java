
package lk.sasindi.smartcity360.patterns.composite;

public class District extends AbstractCompositeComponent {

    public District(
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
                "Street"
        );
    }

    @Override
    public String getType() {
        return "District";
    }
}
