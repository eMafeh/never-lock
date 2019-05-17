package dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class TransMsg implements Serializable {
    private static final long serialVersionUID = 1L;
    public final UserDto getter;
    public final String msg;
}
